package hunternif.mc.impl.atlas.structure;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hunternif.mc.impl.atlas.AntiqueAtlasMod;
import hunternif.mc.impl.atlas.resource.ResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

public class JigsawConfig implements ResourceReloadListener<Map<Identifier, StructurePieceTile>> {
    private static final Identifier ID = AntiqueAtlasMod.id("structures");

    public static final Map<Identifier, StructurePieceTile> PIECES = new ConcurrentHashMap<>();

    private static JsonObject readResource(ResourceManager manager, Identifier id) throws IOException {
        Resource resource = manager.getResource(id).orElseThrow(IOException::new);
        try (InputStream stream = resource.getInputStream(); InputStreamReader reader = new InputStreamReader(stream)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        }
    }

    private static StructurePieceTile parseJson(JsonObject json) {
        int version = json.getAsJsonPrimitive("version").getAsInt();

        if (version == 1) {
            return new StructurePieceTile(
                    Identifier.tryParse(json.get("tile").getAsString()),
                    json.get("priority").getAsInt()
            );
        } else if (version == 2) {
            return new StructurePieceTileXZ(
                    Identifier.tryParse(json.get("tile_x").getAsString()),
                    Identifier.tryParse(json.get("tile_z").getAsString()),
                    json.get("priority").getAsInt()
            );
        } else {
            throw new RuntimeException("Unsupported JSON version: " + version + ". Only version 1 is supported.");
        }
    }

    @Override
    public CompletableFuture<Map<Identifier, StructurePieceTile>> load(ResourceManager manager, Profiler
            profiler, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            Map<Identifier, StructurePieceTile> pieces = new HashMap<>();


            try {
                for (Identifier id : manager.findResources("atlas/structures", id -> id.toString().endsWith(".json")).keySet()) {
                    // id now contains the physical file path of the structure piece
                    AntiqueAtlasMod.LOG.info("Found structure piece config: " + id);

                    try {
                        // strip parts to get a better id
                        Identifier piece_id = new Identifier(
                                id.getNamespace(),
                                id.getPath().replace("atlas/structures/", "").replace(".json", "")
                        );

                        JsonObject json = readResource(manager, id);
                        pieces.put(piece_id, parseJson(json));
                    } catch (Exception e) {
                        AntiqueAtlasMod.LOG.warn("Error reading structure piece config from " + id, e);
                    }
                }

            } catch (Throwable e) {
                AntiqueAtlasMod.LOG.warn("Failed to read structure piece mapping from data pack!", e);
            }

            return pieces;

        }, executor);
    }

    @Override
    public CompletableFuture<Void> apply(Map<Identifier, StructurePieceTile> pieces, ResourceManager
            manager, Profiler profiler, Executor executor) {
        return CompletableFuture.runAsync(() -> {
            pieces.forEach((id, piece) -> {

                AntiqueAtlasMod.LOG.info("Apply structure piece config: " + id);
                if (piece instanceof StructurePieceTileXZ) {
                    StructureHandler.registerJigsawTile(id, piece.getPriority(), piece.getTileX(), StructureHandler::IF_X_DIRECTION);
                    StructureHandler.registerJigsawTile(id, piece.getPriority(), piece.getTileZ(), StructureHandler::IF_Z_DIRECTION);
                } else {
                    StructureHandler.registerJigsawTile(id, piece.getPriority(), piece.getTile());
                }
            });
        }, executor);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Collection<Identifier> getDependencies() {
        return Collections.emptyList();
    }
}
