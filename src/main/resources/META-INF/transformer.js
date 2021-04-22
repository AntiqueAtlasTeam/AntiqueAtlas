var Opcodes = Java.type('org.objectweb.asm.Opcodes');
var InsnList = Java.type("org.objectweb.asm.tree.InsnList");
var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');
var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
var TypeInsnNode = Java.type('org.objectweb.asm.tree.TypeInsnNode');
var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
var LdcInsnNode = Java.type('org.objectweb.asm.tree.LdcInsnNode');
var InvokeDynamicInsnNode = Java.type('org.objectweb.asm.tree.InvokeDynamicInsnNode');
var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
var FrameNode = Java.type('org.objectweb.asm.tree.FrameNode');
var LineNumberNode = Java.type('org.objectweb.asm.tree.LineNumberNode');

function initializeCoreMod() {

    return {

        // Structure Event Patch
        'structure_start_patch': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.world.gen.feature.structure.StructureStart'
            },
            'transformer': function(classNode) {
                patchMethod([{
                    obfName: "func_230366_a_",
                    name: "func_230366_a_",
                    desc: "(Lnet/minecraft/world/ISeedReader;Lnet/minecraft/world/gen/feature/structure/StructureManager;Lnet/minecraft/world/gen/ChunkGenerator;Ljava/util/Random;Lnet/minecraft/util/math/MutableBoundingBox;Lnet/minecraft/util/math/ChunkPos;)V",
                    patches: [patchStructureStartfunc_230366_a_1, patchStructureStartfunc_230366_a_2]
                }], classNode, "StructureStart");
                return classNode;
            }
        },
		// Forge's event doesn't include the slot involved in the crafting, so we do this ourselves
        'crafting_result_slot_patch': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.inventory.container.CraftingResultSlot'
            },
            'transformer': function(classNode) {
                patchMethod([{
                    obfName: "func_75208_c",
                    name: "onCrafting",
                    desc: "(Lnet/minecraft/item/ItemStack;)V",
                    patches: [patchCraftingResultSlotOnCrafting]
                }], classNode, "CraftingResultSlot");
                return classNode;
            }
        }
    };
}

function patchMethod(entries, classNode, name) {

    log("Patching " + name + "...");
    for (var i = 0; i < entries.length; i++) {

        var entry = entries[i];
        var method = findMethod(classNode.methods, entry);
        var flag = !!method;
		debug((flag ? "Run" : "Dont Run"));
        if (flag) {

            var obfuscated = !method.name.equals(entry.name);
            for (var j = 0; j < entry.patches.length; j++) {
				var flag2 = true;
                var patch = entry.patches[j];
                if (!patchInstructions(method, patch.filter, patch.action, obfuscated)) {
					flag2 = false;
                    flag = false;
                }
				log("Patch " + (j+1) + (flag2 ? " was successful" : " failed"));
            }
        }

        log("Patching " + name + "#" + entry.name + (flag ? " was successful" : " failed"));
    }
}

function findMethod(methods, entry) {

    for (var i = 0; i < methods.length; i++) {

        var method = methods[i];
        if ((method.name.equals(entry.obfName) || method.name.equals(entry.name)) && method.desc.equals(entry.desc)) {

            return method;
        }
    }
}

function patchInstructions(method, filter, action, obfuscated) {

    var instructions = method.instructions.toArray();
    for (var i = 0; i < instructions.length; i++) {

        var node = filter(instructions[i], obfuscated);
		debug("FILTER NODE: "+(!!node));
        if (!!node) {

            break;
        }
    }
	debug("ACTION NODE: "+(!!node));
    if (!!node) {
		debug("Taken Action");
        action(node, method.instructions, obfuscated);
        return true;
    }
}

var patchStructureStartfunc_230366_a_1 = {
    filter: function(node, obfuscated) {
        if (matchesHook(node, "net/minecraft/world/gen/feature/structure/StructureStart", "func_202500_a", "recalculateStructureSize", "()V")) {
            return node;
        }
    },
    action: function(node, instructions, obfuscated) {
        var insnList = new InsnList();
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 1));
        insnList.add(generateHook("onStructureAddedHook", "(Lnet/minecraft/world/gen/feature/structure/StructureStart;Lnet/minecraft/world/ISeedReader;)V"));
        instructions.insert(node, insnList);
    }
};

var patchStructureStartfunc_230366_a_2 = {
    filter: function(node, obfuscated) {
        if (matchesHook(node, "net/minecraft/world/gen/feature/structure/StructurePiece", "func_230383_a_", "func_230383_a_", "(Lnet/minecraft/world/ISeedReader;Lnet/minecraft/world/gen/feature/structure/StructureManager;Lnet/minecraft/world/gen/ChunkGenerator;Ljava/util/Random;Lnet/minecraft/util/math/MutableBoundingBox;Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/util/math/BlockPos;)Z")) {
            return node;
        }
    },
    action: function(node, instructions, obfuscated) {
        var insnList = new InsnList();
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 12));
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 1));
        insnList.add(generateHook("onStructurePieceAddedHook", "(ZLnet/minecraft/world/gen/feature/structure/StructurePiece;Lnet/minecraft/world/ISeedReader;)Z"));
        instructions.insert(node, insnList);
    }
};

var patchCraftingResultSlotOnCrafting = {
    filter: function(node, obfuscated) {
		debug("");
		debug("*****************");
		if(node != null){
			if(!!node.owner) debug("Node Owner: "+node.owner);
			if(!!node.name) debug("Node Name: "+node.name);
			if(!!node.desc) debug("Node Desc: "+node.desc);
			if(!!node.getOpcode()) debug("Node Opcodes: "+node.getOpcode());
			if(!!node.var) debug("Node Var: "+node.var);	
		}
		debug("*****************");
		debug("");
        if (matchesHook(node, "net/minecraft/item/ItemStack", "func_77980_a", "onCrafting", "(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;I)V")) {
            return node;
        }
    },
    action: function(node, instructions, obfuscated) {
        var insnList = new InsnList();
		insnList.add(new VarInsnNode(Opcodes.ALOAD, null));
        insnList.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/inventory/container/CraftingResultSlot", obfuscated ? "field_75238_b" : "player", "Lnet/minecraft/entity/player/PlayerEntity;"));
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 1));
		insnList.add(new VarInsnNode(Opcodes.ALOAD, null));
        insnList.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/inventory/container/CraftingResultSlot", obfuscated ? "field_75239_a" : "craftMatrix", "Lnet/minecraft/inventory/CraftingInventory;"));
        insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
        insnList.add(generateHook("firePlayerCraftingEvent", "(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/inventory/CraftingInventory;Lnet/minecraft/inventory/container/CraftingResultSlot;)V"));
		
		debug("#########MC#########");
		if(!!node.owner)debug("MC Node Owner: "+node.owner);
		debug("MC Node Name: "+node.name);
		debug("MC Node Desc: "+node.desc);
		debug("MC Node Is Var: "+(node instanceof VarInsnNode));
		debug("MC Node Is Method: "+(node instanceof MethodInsnNode));
		debug("MC Node Opcodes: "+node.getOpcode());
		debug("MC Node Var: "+node.var);	
		debug("#########MC#########");
		var node2 = insnList.get(0);
		debug("#########CT#########");
		if(!!node2.owner)debug("CT Node Owner: "+node2.owner);
		debug("CT Node Name: "+node2.name);
		debug("CT Node Desc: "+node2.desc);
		debug("CT Node Is Var: "+(node2 instanceof VarInsnNode));
		debug("CT Node Is Method: "+(node2 instanceof MethodInsnNode));
		debug("CT Node Opcodes: "+node2.getOpcode());
		debug("CT Node Var: "+node2.var);	
		debug("#########CT#########");
		
        instructions.insert(node, insnList);
    }
};

function matchesHook(node, owner, name, obfName, desc) {
	
	return !!node.owner && !!node.name && !!node.desc && matchesNode(node, owner, name, obfName, desc);
}

function matchesMethod(node, owner, name, obfName, desc) {

    return node instanceof MethodInsnNode && matchesNode(node, owner, name, obfName, desc);
}

function matchesField(node, owner, name, obfName, desc) {

    return node instanceof FieldInsnNode && matchesNode(node, owner, name, obfName, desc);
}

function matchesNode(node, owner, name, obfName, desc) {

    return node.owner.equals(owner) && (node.name.equals(name) || node.name.equals(obfName)) && node.desc.equals(desc);
}

function generateHook(name, desc) {

    return new MethodInsnNode(Opcodes.INVOKESTATIC, "hunternif/mc/impl/atlas/forge/hook/AntiqueAtlasHooks", name, desc, false);
}

function getNthNode(node, n) {

    for (var i = 0; i < Math.abs(n); i++) {

        if (n < 0) {

            node = node.getPrevious();
        } else {

            node = node.getNext();
        }
    }

    return node;
}

function log(message) {

    print("[Antique Atlas Transformer]: " + message);
}

function debug(message) {
	if (false) {
		print("[Antique Atlas Transformer Debug]: " + message);
	}
}