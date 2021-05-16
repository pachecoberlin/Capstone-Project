package de.pacheco.bakingapp;

import de.pacheco.bakingapp.model.Recipe;
import de.pacheco.bakingapp.model.Step;

import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static de.pacheco.bakingapp.utils.UtilsKt.getNextStep;
import static de.pacheco.bakingapp.utils.UtilsKt.getPreviousStep;
import static de.pacheco.bakingapp.utils.UtilsKt.getStep;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class UtilsTest {
    private List<Step> steps;
    private Recipe recipe;

    @Before
    public void init() {
        steps = new LinkedList<>();
        for (int i = 1; i < 5; i++) {
            steps.add(new Step(i));
        }
        for (int i = 8; i < 10; i++) {
            steps.add(new Step(i));
        }
        recipe = new Recipe(steps);
    }

    @Test
    public void getStepTest() {
        for (int i = 1; i < 5; i++) {
            Step step = getStep(steps, i);
            assert step != null;
            assertEquals(i, step.id);
        }
        Step step = getStep(steps, 23);
        assertNull(step);
    }

    @Test
    public void getNextStepTest() {
        Step step = getNextStep(1, recipe);
        assertEquals(2, step.id);
        step = getNextStep(5, recipe);
        assertEquals(8, step.id);
        step = getNextStep(10, recipe);
        assertEquals(1, step.id);
    }

    @Test
    public void getPreviousStepTest() {
        Step step = getPreviousStep(1, recipe);
        assertEquals(1, step.id);
        step = getPreviousStep(8, recipe);
        assertEquals(4, step.id);
        step = getPreviousStep(9, recipe);
        assertEquals(8, step.id);
    }
}