/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 */

package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.arguments.CompoundNbtTagArgumentType;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.utils.misc.text.MeteorClickEvent;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.component.ComponentMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class NbtCommand extends Command {
    public NbtCommand() {
        super("nbt", "Modifies NBT data for an item, example: .nbt add {display:{Name:'{\"text\":\"$cRed Name\"}'}}");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            error("This command is not yet updated for 1.20.5 and above!");
            return SINGLE_SUCCESS;
        });

        // TODO: Update using Components over NBT
        /*builder.then(literal("add").then(argument("nbt", CompoundNbtTagArgumentType.create()).executes(s -> {
            ItemStack stack = mc.player.getInventory().getMainHandStack();

            if (validBasic(stack)) {
                NbtCompound tag = CompoundNbtTagArgumentType.get(s);

                if (tag != null) {
                    ItemStack newStack = ItemStack.fromNbtOrEmpty(mc.world.getRegistryManager(), tag);
                    newStack.applyComponentsFrom(stack.getComponents());

                    setStack(newStack);
                } else {
                    error("Some of the NBT data could not be found, try using: " + Config.get().prefix.get() + "nbt set {nbt}");
                }
            }

            return SINGLE_SUCCESS;
        })));

        builder.then(literal("set").then(argument("nbt", CompoundNbtTagArgumentType.create()).executes(context -> {
            ItemStack stack = mc.player.getInventory().getMainHandStack();

            if (validBasic(stack)) {
                stack = ItemStack.fromNbtOrEmpty(mc.world.getRegistryManager(), CompoundNbtTagArgumentType.get(context));
                setStack(stack);
            }

            return SINGLE_SUCCESS;
        })));

        builder.then(literal("remove").then(argument("nbt_path", NbtPathArgumentType.nbtPath()).executes(context -> {
            ItemStack stack = mc.player.getInventory().getMainHandStack();

            if (validBasic(stack)) {
                NbtPathArgumentType.NbtPath path = context.getArgument("nbt_path", NbtPathArgumentType.NbtPath.class);
                path.remove(stack.encode(mc.world.getRegistryManager()));
            }

            return SINGLE_SUCCESS;
        })));

        builder.then(literal("get").executes(context -> {
            ItemStack stack = mc.player.getInventory().getMainHandStack();

            if (stack == null) {
                error("You must hold an item in your main hand.");
            } else {
                ComponentMap components = stack.getComponents();

                MutableText copyButton = Text.literal("NBT");
                copyButton.setStyle(copyButton.getStyle()
                    .withFormatting(Formatting.UNDERLINE)
                    .withClickEvent(new MeteorClickEvent(
                        ClickEvent.Action.RUN_COMMAND,
                        this.toString("copy")
                    ))
                    .withHoverEvent(new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        Text.literal("Copy the NBT data to your clipboard.")
                    )));

                MutableText text = Text.literal("");
                text.append(copyButton);

                if (components == null) text.append("{}");
                else text.append(" ").append(Text.of(components.toString()));

                info(text);
            }

            return SINGLE_SUCCESS;
        }));

        builder.then(literal("copy").executes(context -> {
            ItemStack stack = mc.player.getInventory().getMainHandStack();

            if (stack == null) {
                error("You must hold an item in your main hand.");
            } else {
                ComponentMap components = stack.getComponents();
                mc.keyboard.setClipboard(components.toString());
                MutableText nbt = Text.literal("NBT");
                nbt.setStyle(nbt.getStyle()
                    .withFormatting(Formatting.UNDERLINE)
                    .withHoverEvent(new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        Text.of(components.toString())
                    )));

                MutableText text = Text.literal("");
                text.append(nbt);
                text.append(Text.literal(" data copied!"));

                info(text);
            }

            return SINGLE_SUCCESS;
        }));

        builder.then(literal("paste").executes(context -> {
            ItemStack stack = mc.player.getInventory().getMainHandStack();

            if (validBasic(stack)) {
                NbtCompound nbt = CompoundNbtTagArgumentType.create().parse(new StringReader(mc.keyboard.getClipboard()));

                stack = ItemStack.fromNbtOrEmpty(mc.world.getRegistryManager(), nbt);

                setStack(stack);
            }

            return SINGLE_SUCCESS;
        }));

        builder.then(literal("count").then(argument("count", IntegerArgumentType.integer(-127, 127)).executes(context -> {
            ItemStack stack = mc.player.getInventory().getMainHandStack();

            if (validBasic(stack)) {
                int count = IntegerArgumentType.getInteger(context, "count");
                stack.setCount(count);
                setStack(stack);
                info("Set mainhand stack count to %s.", count);
            }

            return SINGLE_SUCCESS;
        })));*/
    }

    private void setStack(ItemStack stack) {
        mc.player.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(36 + mc.player.getInventory().selectedSlot, stack));
    }

    private boolean validBasic(ItemStack stack) {
        if (!mc.player.getAbilities().creativeMode) {
            error("Creative mode only.");
            return false;
        }

        if (stack == null) {
            error("You must hold an item in your main hand.");
            return false;
        }
        return true;
    }
}
