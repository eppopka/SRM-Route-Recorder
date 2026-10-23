import com.google.gson.JsonParser;
import io.github.christechs.routerec.RouteRecorderServer;
import io.github.christechs.routerec.manager.RoomManager;
import net.hollowcube.polar.PolarLoader;
import net.minestom.server.MinecraftServer;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;

/** Run from the server directory with the full server JAR on the classpath. */
public class CompatibilitySmoke {
    static void varInt(DataOutputStream out, int value) throws IOException {
        do { int bits = value & 127; value >>>= 7; out.writeByte(bits | (value == 0 ? 0 : 128)); } while (value != 0);
    }
    static int varInt(DataInputStream in) throws IOException {
        int result = 0;
        for (int i = 0; i < 5; i++) { int b = in.readUnsignedByte(); result |= (b & 127) << (7 * i); if ((b & 128) == 0) return result; }
        throw new IOException("Invalid VarInt");
    }
    static void checkStatus() throws Exception {
        try (Socket socket = new Socket("127.0.0.1", 25565)) {
            socket.setSoTimeout(10000);
            var payload = new ByteArrayOutputStream();
            var packet = new DataOutputStream(payload);
            varInt(packet, 0); varInt(packet, 776);
            byte[] host = "localhost".getBytes(StandardCharsets.UTF_8);
            varInt(packet, host.length); packet.write(host); packet.writeShort(25565); varInt(packet, 1);
            var out = new DataOutputStream(socket.getOutputStream());
            varInt(out, payload.size()); out.write(payload.toByteArray()); out.write(new byte[]{1, 0}); out.flush();
            var in = new DataInputStream(socket.getInputStream());
            varInt(in);
            if (varInt(in) != 0) throw new AssertionError("Unexpected status packet");
            String text = new String(in.readNBytes(varInt(in)), StandardCharsets.UTF_8);
            var version = JsonParser.parseString(text).getAsJsonObject().getAsJsonObject("version");
            if (!version.get("name").getAsString().contains("26.2") || version.get("protocol").getAsInt() != 776)
                throw new AssertionError("Wrong server version: " + version);
            System.out.println("PASS status: " + version);
        }
    }
    public static void main(String[] args) {
        int code = 0;
        try {
            RouteRecorderServer.main(new String[0]);
            checkStatus();
            int worlds = 0, chunks = 0;
            try (var files = Files.list(Path.of("polar_worlds"))) {
                for (Path path : files.filter(p -> p.toString().endsWith(".polar")).toList()) {
                    var loader = new PolarLoader(path);
                    if (loader.world().chunks().isEmpty()) throw new AssertionError("Empty world: " + path);
                    worlds++; chunks += loader.world().chunks().size();
                }
            }
            System.out.println("PASS parse " + worlds + " worlds, " + chunks + " chunks");
            for (String name : new String[]{"Admin", "f7boss"}) {
                var result = RoomManager.loadRoom(name).get(120, TimeUnit.SECONDS);
                if (!result.success() || result.instance() == null || result.spawnPos() == null)
                    throw new AssertionError("Cannot load " + name);
                if (result.instance().getChunks().isEmpty()) throw new AssertionError("No chunks: " + name);
                System.out.println("PASS room load: " + name + " (" + result.instance().getChunks().size() + " chunks)");
                RoomManager.cleanupOldInstance();
            }
            System.out.println("ALL COMPATIBILITY SMOKE CHECKS PASSED");
        } catch (Throwable t) { t.printStackTrace(); code = 1; }
        finally { try { MinecraftServer.stopCleanly(); } catch (Throwable ignored) {} }
        System.exit(code);
    }
}
