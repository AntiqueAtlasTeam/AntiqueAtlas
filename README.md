Antique Atlas
=============

Adds item Antique Atlas. It's like a fancy antique-looking map, which saves all visited locations so you can browse them.
Maps are generated around the player by finding average biome IDs in a chunk. All data is saved on the server.

You can export the contents of an Atlas in the current dimension to a PNG image.

Also you can edit the configs to set which biome uses what texture, or even assign your own textures to any biome, including custom mod biomes. See the wiki for a tutorial: https://github.com/Hunternif/AntiqueAtlas/wiki/Editing-Textures

API
===

Use the class `hunternif.mc.atlas.api.AtlasAPI` to obtain a reference to the API. There are 3 actual APIs: BiomeAPI, TileAPI and MarkerAPI. See the javadocs and the wiki for more: https://github.com/Hunternif/AntiqueAtlas/wiki/API
