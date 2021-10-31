package cz.speedy.mccollector.utils;

import cz.speedy.mccollector.McCollector;
import cz.speedy.mccollector.objects.MinecraftStatus;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MinecraftUtil {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    private MinecraftUtil() { }

    public static CompletableFuture<MinecraftStatus> getServerInfo(String address, int port, int timeout)  {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Socket socket = new Socket();
                socket.setSoTimeout(timeout);
                socket.connect(new InetSocketAddress(address, port), timeout);

                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

                ByteArrayOutputStream handshake_bytes = new ByteArrayOutputStream();
                DataOutputStream handshake_output = new DataOutputStream(handshake_bytes);

                handshake_output.writeByte(0x00);
                writeVarInt(handshake_output, 47);
                writeVarInt(handshake_output, address.length());
                handshake_output.writeBytes(address);
                handshake_output.writeShort(port);
                writeVarInt(handshake_output, 1);

                writeVarInt(dataOutputStream, handshake_bytes.size());
                dataOutputStream.write(handshake_bytes.toByteArray());

                dataOutputStream.writeByte(0x01);
                dataOutputStream.writeByte(0x00);

                readVarInt(dataInputStream);
                int j = readVarInt(dataInputStream);

                if (j == -1) {
                    throw new IOException("unexpected end of stream.");
                } else if (j != 0) {
                    throw new IOException("wrong packet.");
                }

                int k = readVarInt(dataInputStream);
                if (k == -1) {
                    throw new IOException("unexpected end of stream.");
                } else if (k == 0) {
                    throw new IOException("wrong byte length.");
                }

                byte[] arrayOfByte = new byte[k];
                dataInputStream.readFully(arrayOfByte);

                handshake_output.close();
                handshake_bytes.close();
                dataInputStream.close();
                dataOutputStream.close();
                socket.close();
                return McCollector.GSON.fromJson(new String(arrayOfByte, StandardCharsets.UTF_8), MinecraftStatus.class);
            } catch (IOException exception) {
                throw new CompletionException(exception);
            }
        }, EXECUTOR_SERVICE);
    }

    private static void writeVarInt(DataOutputStream out, int paramInt) throws IOException {
        while ((paramInt & ~0x7F) != 0) {
            out.writeByte((paramInt & 0x7F) | 0x80);
            paramInt >>>= 7;
        }

        out.writeByte(paramInt);
    }

    private static int readVarInt(DataInputStream in) throws IOException {
        int value = 0;
        int size = 0;
        int b;
        while (((b = in.readByte()) & 0x80) == 0x80) {
            value |= (b & 0x7F) << (size++ * 7);
            if (size > 5) {
                throw new IOException("VarInt too long (length must be <= 5)");
            }
        }

        return value | ((b & 0x7F) << (size * 7));
    }
}
