# Embedded Acce Staging Directory

This directory contains a **mechanically pre-processed** copy of
`accessories-1.20.1-Backport-reference/common/src/main/{java,resources}`,
prepared for incremental porting into the bridge mod proper.

## What's been done

- 165 Java files copied with package path rewritten:
  - `io.wispforest.accessories.*` → `com.mangzai.curiotrinketbridge.embeddedacce.*`
- 102 resource files copied with namespace folder renamed:
  - `data/accessories/` → `data/ctb_acce/`
  - `assets/accessories/` → `assets/ctb_acce/`
- 23 JSON/mcmeta files have inline `"accessories:..."` references rewritten to `"ctb_acce:..."`
- `Accessories.MODID` constant changed from `"accessories"` to `"ctb_acce"`
- Original source layout preserved otherwise

## What's NOT been done (next conversation must do per file as moved into src)

These can't be machine-rewritten safely in bulk:

1. **Architectury platform layer** — many files use `dev.architectury.*` / `@ExpectPlatform`
   - Replace with direct Forge API calls
2. **fabric-api imports** — `net.fabricmc.fabric.api.*`
   - Replace with Forge equivalents (event bus / capabilities / reload listener)
3. **Auto-config (cloth-config) imports** — `me.shedaniel.autoconfig.*`
   - Either bring in cloth-config, or strip config system (use Forge ConfigSpec)
4. **endec library** — `io.wispforest.endec.*`
   - Either internal-vendor a minimal endec subset, or replace serialization with NBT/Codec
5. **Accessories.attemptOpenScreenPlayer / openAccessoriesMenu** — Fabric networking
   - Replace with Forge `NetworkHooks.openScreen` + SimpleChannel
6. **mixin pond/access-widener targets**
   - Add equivalent mixins in bridge `mixins.json` or use Forge accessors

## Dependency cone for "minimum UI working" goal

Recommended order to migrate from staging into `src/main/java/com/mangzai/curiotrinketbridge/embeddedacce/`:

1. `api/slot/SlotType.java`, `SlotGroup.java`, `SlotEntryReference.java`, `SlotTypeReference.java`
2. `impl/SlotTypeImpl.java`, `impl/SlotGroupImpl.java`
3. `impl/ExpandedSimpleContainer.java`
4. `api/AccessoriesContainer.java`, `api/AccessoriesCapability.java` (interfaces; can stub with Curios-backed impl)
5. `client/AccessoriesMenu.java` + `api/menu/AccessoriesBasedSlot.java` + `api/menu/AccessoriesSlotGenerator.java`
6. `client/gui/AccessoriesScreen.java` + `client/GuiGraphicsUtils.java` + `client/gui/AccessoriesInternalSlot.java` + `client/gui/ToggleButton.java`
7. cclayer: write `CuriosSlotMapper`, `CuriosBackedAccessoriesContainer`, `OpenScreenInterceptor`

Each file moved into src must have its dependencies satisfied (other moved files or stub
interfaces). When you hit a missing class, either:
- Pull it in from staging (if simple enough)
- Stub it with a minimal Forge-friendly implementation

## Build status

This directory is **NOT** in any Gradle source set. It's purely reference material.
Verify by: `.\gradlew build` should succeed without compiling any file from here.

## Reference: original sources

Untouched original at `../../accessories-1.20.1-Backport-reference/common/src/main/`.
Compare side-by-side when porting.
