/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.event;

public final class DevToolsEvent extends Event {

    public DevToolsEvent() {
    }

    public static void fire() {
        Event.publish(new DevToolsEvent());
    }
}
