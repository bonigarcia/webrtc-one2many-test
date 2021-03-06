/*
 * (C) Copyright 2017 Boni Garcia (http://bonigarcia.github.io/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package io.github.bonigarcia;

import static io.github.bonigarcia.Configuration.viewersRate;
import static io.github.bonigarcia.Configuration.numViewers;
import static io.github.bonigarcia.Configuration.sessionTime;

/**
 * Calculate average formula.
 *
 * @author Boni Garcia (boni.garcia@urjc.es)
 * @since 1.0.0
 */
public class CalculateAverageFormula {

    public static void main(String[] args) {
        int totalTime = viewersRate * numViewers + sessionTime;
        int numSamples = totalTime / viewersRate;

        String formula = "=AVERAGE(A%d:A%d)";
        int x, y;
        for (int i = 0; i < numSamples; i++) {
            x = i * viewersRate + 2;
            y = x + viewersRate - 1;
            System.out.println(String.format(formula, x, y, x, y));
        }
    }

}
