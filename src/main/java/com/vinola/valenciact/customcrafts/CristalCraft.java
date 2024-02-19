package com.vinola.valenciact.customcrafts;

import com.vinola.valenciact.ValenciaCT;
import com.vinola.valenciact.customitems.Cristal;
import com.vinola.valenciact.customitems.PepitaCristal;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

public class CristalCraft {
    private final PepitaCristal pepCristal;
    private final Cristal cristal;

    public CristalCraft(){
        pepCristal = new PepitaCristal();
        cristal = new Cristal(true);
        registerCristalRecipe();
    }

    private void registerCristalRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(ValenciaCT.getInstance(), "Cristal"), cristal.getCristal());
        recipe.shape(" P ",
                     "PPP",
                     " P ");
        recipe.setIngredient('P', new RecipeChoice.ExactChoice(pepCristal.getPepCristal()));

        Bukkit.addRecipe(recipe);
    }

}
