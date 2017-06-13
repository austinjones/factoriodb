package com.factoriodb.chain;

import com.factoriodb.chain.assembler.Assembler;
import com.factoriodb.chain.option.EntityOption;
import com.factoriodb.model.ItemStack;
import com.factoriodb.model.ItemsStack;
import com.factoriodb.model.Model;
import com.factoriodb.model.Recipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class Crafter extends Entity {
    private Recipe r;
    private Assembler assembler;

    public Crafter(Recipe r) {
        this.r = r;
        this.assembler = Assembler.getInstance(r);
    }


    public Crafter(Model m, String itemName) {
        List<Recipe> recipes = m.getRecipeByResult(itemName);
        if(recipes.size() == 0 || recipes.size() > 1) {
            throw new IllegalStateException("Unique recipe not found for name " + itemName);
        }

        this.r = recipes.get(0);
        this.assembler = Assembler.getInstance(r);
    }

    public String getAssemblerType() {
        // TODO: enum
        return "assembler";
    }

    public Recipe getRecipe() {
        return r;
    }

    @Override
    public ItemsStack getInputRatio() {
        ItemsStack input = r.getInput();
        ItemsStack normalized = ItemsStack.mul(input, 1.0 / input.total());
        return normalized;
    }

    @Override
    public ItemsStack getOutputRatio() {
        ItemsStack input = r.getOutput();
        ItemsStack normalized = ItemsStack.mul(input, 1.0 / input.total());
        return normalized;
    }

    @Override
    public ItemsStack getOutputRatio(ItemsStack availableInput) {
        ItemsStack entityInput = getInputRatio();
        ItemsStack usableRequest = availableInput.filter(entityInput.itemNames());
        double rate = usableRequest.minratio(entityInput);
        return ItemsStack.mul(getOutputRatio(), rate);
    }

    @Override
    public String toString() {
        return getAssemblerType() + r.getOutput();
    }

    @Override
    public Collection<? extends EntityOption> options() {
        return assembler.options(this);
    }

    @Override
	public Optional<Crafter> getCrafter() {
		return Optional.of(this);
	}
	
}
