# SRM Route Recorder — Minecraft Java 26.2

## Run
1. Back up your current server folder, especially routes/ and polar_worlds/.
2. Extract this release into a NEW folder. Java 25 is required.
3. On Windows, double-click start.bat. On Linux/macOS, run ./start.sh.
4. Join with Minecraft Java Edition 26.2 at localhost:25565 on the same machine.

Manual launch:
    java -Xmx4G -jar Minestom-Dungeons-26.2-all.jar

If updating an existing installation, keep your own routes/ and polar_worlds/;
do not overwrite newer recordings with the bundled data. This bundle contains
unchanged worlds and routes from eppopka/SRM-Route-Recorder branch 26-2.

The server retains upstream offline authentication and binds to 0.0.0.0:25565.
Use only on a trusted/local network; do not expose it publicly as configured.
No client mods are required. The optional debug renderer mod is separate and
has NOT been upgraded or tested here; use a compatible release or play without it.

## Changes
- Minestom: 2026.03.25-1.21.11 -> 2026.10.07-26.2.
- Polar world loader: 1.15.0 -> 1.16.1 (targets the same Minestom release).
- Adapted removed block property methods to air(), liquid(), and solid().
- Adapted team packet construction to TeamsPacket.Settings and TeamColor.
- Build/artifact version: 26.2. Java requirement remains 25.
- Added a repeatable protocol/world smoke-test utility.

## Verification
- Gradle shadowJar compiled and packaged successfully with Java 25.
- Real TCP status request: version name 26.2, protocol 776.
- Parsed all 132 original Polar worlds (1014 chunks).
- Loaded Admin (16 chunks) and f7boss (156 chunks) through RoomManager.
- Original bundled route and world files are byte-for-byte unchanged.
- No interactive Minecraft-client gameplay, recording, or debug-mod testing.
- Only the full JAR was built/tested; no ProGuard-minified JAR is included.

## Source / rebuild
Source base: ChrisTechs/SRM-Route-Recorder
Commit: c442fc1a68354a89d4b514987b06d9a4cde7bbab
Bundled data: eppopka/SRM-Route-Recorder, branch 26-2
Commit: d9f3d80c8653da880a0d525bf06ce9e4c61b9aab

This is a rebuild from the supplied upstream source, not a binary patch of the
fork JAR. Any unpublished code changes exclusive to that JAR are not included.
No changes have been pushed to GitHub.

With JDK 25 installed, from the updated source directory:
    ./gradlew shadowJar
On Windows:
    gradlew.bat shadowJar
Artifact: build/libs/Minestom-Dungeons-26.2-all.jar

To repeat smoke checks using this bundle's data, from the extracted server
folder with the updated source tools/ folder available:
    javac -cp Minestom-Dungeons-26.2-all.jar tools/CompatibilitySmoke.java
Windows:
    java -Xmx2G -cp "tools;Minestom-Dungeons-26.2-all.jar" CompatibilitySmoke
Linux/macOS:
    java -Xmx2G -cp "tools:Minestom-Dungeons-26.2-all.jar" CompatibilitySmoke
Stop any existing server on port 25565 first. No Minecraft client is needed.
