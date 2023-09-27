package com.igrium.scaffold.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.igrium.scaffold.LevelEditor;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {

    protected TitleScreenMixin(Text title) {
        super(title);
    }
    
    @Inject(method = "init()V", at = @At("RETURN"))
	protected void init(CallbackInfo ci) { 
		this.addDrawableChild(ButtonWidget.builder(Text.translatable("menu.scaffoldeditor"), (button) -> {
            new LevelEditor().launch(client);
        }).build());
	}
}
