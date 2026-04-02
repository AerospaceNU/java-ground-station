package com.nuli.groundstation;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import com.nuli.proto.NULIMessageProto.NULIMessage;
import com.nuli.proto.NULIMessageProto.NULIMessage.Commands_Parameters;
import com.nuli.proto.NULIMessageProto.NULIMessage.Commands_Response;
import com.nuli.groundstation.PacketUtils.NULIMessageCodec;



/**
 * Sends GetTelemetry commands to the board and delivers live responses.
 *
 * Uses your existing PacketUtils + NULIMessageCodec for framing,
 * and a BoardTransport for the physical link.
 *
 * Usage:
 *   PacketUtils packetUtils = new PacketUtils();
 *   NULIMessageCodec codec = new NULIMessageCodec();
 *   BoardTransport transport = new MySerialTransport("/dev/ttyUSB0");
 *
 *   TelemetryCommandService service = new TelemetryCommandService(packetUtils, codec, transport);
 *   service.startTelemetryPolling(100, data -> {
 *       SwingUtilities.invokeLater(() -> updateGui(data));
 *   });
 */
public class TelemetryCommandService {

    private final PacketUtils packetUtils;
    private final NULIMessageCodec codec;
    private final BoardTransport transport;
    private final AtomicInteger messageIdCounter = new AtomicInteger(0);

    private ScheduledExecutorService pollingExecutor;
    private volatile boolean polling = false;

    public TelemetryCommandService(PacketUtils packetUtils,
                                   NULIMessageCodec codec,
                                   BoardTransport transport) {
        this.packetUtils = packetUtils;
        this.codec = codec;
        this.transport = transport;
    }

    // =======================================================================
    //  Generic send/receive — works for ANY command
    // =======================================================================

    /**
     * Sends a NULIMessage request and returns the decoded response.
     *
     * Flow: build NULIMessage → PacketUtils.encode(msg, codec) → transport
     *       → transport response → PacketUtils.decode(bytes, codec) → NULIMessage
     */
    public NULIMessage sendCommand(NULIMessage request) throws Exception {
        byte[] packet = packetUtils.encode(request, codec);
        byte[] responsePacket = transport.sendAndReceive(packet);
        return packetUtils.decode(responsePacket, codec);
    }

    // =======================================================================
    //  Command builders
    // =======================================================================

    /** Builds a GetTelemetry request. The empty params message tells the
     *  board WHICH command to run (via the oneof field being set). */
    public NULIMessage buildGetTelemetryRequest() {
        NULIMessage.Commands_Parameters params =
                NULIMessage.Commands_Parameters.newBuilder()
                        .setGetTelemetryParams(
                                NULIMessage.Commands_Parameters
                                        .Command_GetTelemetry_Parameters.getDefaultInstance())
                        .build();

        return NULIMessage.newBuilder()
                .setMessageId(messageIdCounter.incrementAndGet())
                .setTimestampMs((int) (System.currentTimeMillis() & 0xFFFFFFFFL))
                .setCommandsParameters(params)
                .build();
    }

    public NULIMessage buildPingRequest() {
        NULIMessage.Commands_Parameters params =
                NULIMessage.Commands_Parameters.newBuilder()
                        .setPingParams(
                                NULIMessage.Commands_Parameters
                                        .Command_Ping_Parameters.getDefaultInstance())
                        .build();

        return NULIMessage.newBuilder()
                .setMessageId(messageIdCounter.incrementAndGet())
                .setCommandsParameters(params)
                .build();
    }

    public NULIMessage buildGetGPSRequest() {
        NULIMessage.Commands_Parameters params =
                NULIMessage.Commands_Parameters.newBuilder()
                        .setGetGpsParams(
                                NULIMessage.Commands_Parameters
                                        .Command_GetGPS_Parameters.getDefaultInstance())
                        .build();

        return NULIMessage.newBuilder()
                .setMessageId(messageIdCounter.incrementAndGet())
                .setCommandsParameters(params)
                .build();
    }

    // =======================================================================
    //  Response parsing
    // =======================================================================

    /** Simple container for parsed telemetry values. */
    public static class TelemetryData {
        public final float accelX, accelY, accelZ;
        public final float accelMagnitude;
        public final int returnCode;
        public final long timestampMs;

        public TelemetryData(float ax, float ay, float az,
                             int rc, long ts) {
            this.accelX = ax;
            this.accelY = ay;
            this.accelZ = az;
            this.accelMagnitude = (float) Math.sqrt(ax * ax + ay * ay + az * az);
            this.returnCode = rc;
            this.timestampMs = ts;
        }

        @Override
        public String toString() {
            return String.format(
                "Telemetry[accel=(%.3f, %.3f, %.3f) mag=%.3f rc=%d t=%d]",
                accelX, accelY, accelZ, accelMagnitude, returnCode, timestampMs);
        }
    }

    /**
     * Extracts telemetry from a decoded NULIMessage response.
     * Returns null if the response doesn't contain telemetry data.
     */
    public TelemetryData parseTelemetryResponse(NULIMessage response) {
        if (!response.hasCommandsResponse()) return null;

        Commands_Response cmdResp = response.getCommandsResponse();
        int rc = cmdResp.hasReturnCode() ? cmdResp.getReturnCode().getNumber() : -1;

        if (cmdResp.getResponseCase() !=
                NULIMessage.Commands_Response.ResponseCase.GET_TELEMETRY_RESPONSE) {
            return null;
        }

        Commands_Response.Command_GetTelemetry_Response t =
                cmdResp.getGetTelemetryResponse();

        return new TelemetryData(
                t.getAccelerationXMps2(),
                t.getAccelerationYMps2(),
                t.getAccelerationZMps2(),
                rc,
                response.hasTimestampMs() ? response.getTimestampMs() : 0
        );
    }

    // =======================================================================
    //  Live polling
    // =======================================================================

    /**
     * Starts polling the board for telemetry at a fixed interval.
     *
     * @param intervalMs  polling period (e.g. 100 = 10 Hz)
     * @param onUpdate    callback with parsed data — called on the polling thread,
     *                    so wrap in SwingUtilities.invokeLater() or Platform.runLater()
     */
    public void startTelemetryPolling(int intervalMs, Consumer<TelemetryData> onUpdate) {
        if (polling) {
            throw new IllegalStateException("Already polling");
        }
        polling = true;

        pollingExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "telemetry-poller");
            t.setDaemon(true);
            return t;
        });

        pollingExecutor.scheduleAtFixedRate(() -> {
            if (!polling) return;
            try {
                NULIMessage request = buildGetTelemetryRequest();
                NULIMessage response = sendCommand(request);
                TelemetryData data = parseTelemetryResponse(response);
                if (data != null) {
                    onUpdate.accept(data);
                }
            } catch (Exception e) {
                // Transient errors (serial glitch, CRC mismatch) are expected —
                // log and keep polling
                System.err.println("Telemetry poll error: " + e.getMessage());
            }
        }, 0, intervalMs, TimeUnit.MILLISECONDS);
    }

    public void stopTelemetryPolling() {
        polling = false;
        if (pollingExecutor != null) {
            pollingExecutor.shutdownNow();
            pollingExecutor = null;
        }
    }

    public boolean isPolling() {
        return polling;
    }

    // =======================================================================
    //  Transport interface — implement for your hardware
    // =======================================================================

    public interface BoardTransport {
        /**
         * Sends a framed packet and blocks until a complete response
         * (start flag through stop flag) is received.
         */
        byte[] sendAndReceive(byte[] packet) throws Exception;
    }
}