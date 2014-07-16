/*
 * Copyright 2014 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.real_logic.aeron.mediadriver;

import uk.co.real_logic.aeron.util.Agent;
import uk.co.real_logic.aeron.util.concurrent.AtomicArray;
import uk.co.real_logic.aeron.util.event.EventLogger;

/**
 * Agent that iterates over publications for sending them to registered subscribers.
 */
public class Sender extends Agent
{

    private static final EventLogger LOGGER = new EventLogger(Sender.class);

    private final AtomicArray<DriverPublication> publications;
    private int roundRobinIndex = 0;

    public Sender(final MediaDriver.MediaDriverContext ctx)
    {
        super(ctx.senderIdleStrategy());

        publications = ctx.publications();
    }

    public int doWork()
    {
        try
        {
            roundRobinIndex++;
            if (roundRobinIndex == publications.size())
            {
                roundRobinIndex = 0;
            }

            return publications.doAction(roundRobinIndex, DriverPublication::send);
        }
        catch (final Exception ex)
        {
            LOGGER.logException(ex);
            return 0;
        }
    }

}
