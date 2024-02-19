package com.vinola.valenciact.customcrafts;

import com.vinola.valenciact.ValenciaCT;
import com.vinola.valenciact.customitems.PedraAzul;
import com.vinola.valenciact.customitems.Waystone;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

public class WaystoneCraft {
    private final Waystone waystone;
    private final PedraAzul pedraAzul;

    public WaystoneCraft(){
        waystone = new Waystone();
        pedraAzul = new PedraAzul();
        registerWaystoneRecipe();
    }

    private void registerWaystoneRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(ValenciaCT.getInstance(), "Waystone"), waystone.getWaystone());
        recipe.shape("CCC",
                     "GGG",
                     "SSS");
        recipe.setIngredient('C', new RecipeChoice.ExactChoice(pedraAzul.getPedraAzul()));
        recipe.setIngredient('G', Material.GOLD_INGOT);
        recipe.setIngredient('S', Material.STONE_BRICKS);

        Bukkit.addRecipe(recipe);
    }
}
