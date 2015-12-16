**This is a branch for Minecraft 1.7.10**

Antique Atlas
=============

Antique Atlas is a book that acts like a map featuring infinite scrolling, zoom and custom labeled markers. The map is generated around the player by calculating the average biome in each 16x16 chunk.

Navigate the map by dragging it with the mouse, clicking arrow buttons or pressing arrow keys on the keyboard. Use the +/- keys or mouse wheel to zoom in and out.

You can export the map of the current dimension into a PNG image, see buttons on the right side of the GUI.

You can edit the configs to set which biome uses what texture, or even assign your own textures to any biome, including custom mod biomes. See tutorial on the wiki: https://github.com/Hunternif/AntiqueAtlas/wiki/Editing-Textures

API
===

If you are a mod developer and you wish to interact with AntiqueAtlas, you will need the source code of the API. You can include the whole source code of AntiqueAtlas (the `...-sources.jar` in [Releases](https://github.com/Hunternif/AntiqueAtlas/releases)) which allows you to test the  interaction when debugging your mod.

Use the class `hunternif.mc.atlas.api.AtlasAPI` to obtain a reference to the API. There are 2 actual APIs: TileAPI and MarkerAPI. See javadocs/sources and the wiki for more: https://github.com/Hunternif/AntiqueAtlas/wiki/API
