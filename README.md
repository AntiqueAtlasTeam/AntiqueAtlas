Antique Atlas
=============

Adds item Antique Atlas. It's like a fancy antique-looking map, which saves all visited locations so you can browse them.
Maps are generated around the player by finding average biome IDs in a chunk. All data is saved on the server.

You can export the contents of an Atlas in the current dimension to a PNG image.

Also you can edit the configs to set which biome uses what texture, or even assign your own textures to any biome, including custom mod biomes. See the wiki for a tutorial: https://github.com/Hunternif/AntiqueAtlas/wiki/Editing-Textures

API
===

Use the class `hunternif.mc.atlas.api.AtlasAPI` to obtain a reference to the API. There are 2 actual APIs: BiomeAPI and TileAPI. ___Each texture must be a tilesheet following the Autotile layout!___ _(See the assets and wiki for examples.)_

### BiomeAPI

Allows other mods to register custom textures for biomes. The workflow:

1. During initialization of your mod, _on the client_ set textures for your biome IDs.
2. Save the config, so the textures will be loaded automatically next time, or manually modified by the players.

In order to allow players to manually customize their texture configs, consider using the methods `setTextureIfNone`, which will only set the texture for biome ID if it was not present in the config file. Methods `setTexture` will override the config.

Consider saving the config only if some texture was actually changed (i.e. `setTextureIfNone` returned `true`), so that the config file is not overwritten too often. That makes it easier for players to modify the file manually.

Each texture setter method accepts an array of textures. The purpose of this is to choose a random texture for each tile, so that the map has a better, more varied look.

### TileAPI

Allows other mods to put custom tiles in the Atlases. The workflow:

1. Decide on a unique name for you custom tile (i.e. `zeldaSwordSkillsDungeon`)
2. During initialization of your mod, _on the client_ set textures for that unique name.
3. Anytime during the game, put your custom tile at chunk coordinates.

Use the method `putCustomTile(world, dimension, tileName, chunkX, chunkZ)`. In case of a custom building, the appropriate time to do this is when the building is generated, summoned etc. This will put your custom tile on every Atlas for every player, if they have seen that chunk.

Adjacent tiles with the same tile name will be "stitched" together like biomes. So if you want, for example, to have separate little buildings, you must use different tile names for each building, or make a tilesheet that only consists of single-tile-sized buildings, without transitions.
