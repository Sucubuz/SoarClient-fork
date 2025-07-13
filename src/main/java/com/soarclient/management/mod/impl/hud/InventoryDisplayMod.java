package com.soarclient.management.mod.impl.hud;  
  
import com.soarclient.event.EventBus;  
import com.soarclient.event.client.RenderSkiaEvent;  
import com.soarclient.management.mod.api.hud.HUDMod;  
import com.soarclient.skia.font.Icon;  
import net.minecraft.item.ItemStack;  
  
public class InventoryDisplayMod extends HUDMod {  
  
    public InventoryDisplayMod() {  
        super("mod.inventorydisplay.name", "mod.inventorydisplay.description", Icon.INVENTORY);  
    }  
  
    public final EventBus.EventListener<RenderSkiaEvent> onRenderSkia = event -> {  
        this.draw();  
    };  
  
    private void draw() {  
        float width = 188;  
        float height = 82;  
          
        this.begin();  
        this.drawBackground(getX(), getY(), width, height);  
        this.drawText("Inventory", getX() + 5.5f, getY() + 16f,   
                     com.soarclient.skia.font.Fonts.getRegular(10.5f));  
          
        // Draw inventory items  
        int startX = (int)(this.getX() + 6);  
        int startY = (int)(this.getY() + 22);  
        int index = 0;  
          
        for(int i = 9; i < 36; i++) {  
            ItemStack slot = client.player.getInventory().main.get(i);  
              
            if(slot.isEmpty()) {  
                startX += 20;  
                index += 1;  
                  
                if(index > 8) {  
                    index = 0;  
                    startY += 20;  
                    startX = (int)(this.getX() + 6);  
                }  
                continue;  
            }  
              
            // Use Skia rendering for items instead of RenderUtils  
            // This would need to be implemented based on Soar 8.0's item rendering  
              
            startX += 20;  
            index += 1;  
            if(index > 8) {  
                index = 0;  
                startY += 20;  
                startX = (int)(this.getX() + 6);  
            }  
        }  
          
        this.finish();  
        position.setSize(width, height);  
    }  
  
    @Override  
    public float getRadius() {  
        return 6;  
    }  
}