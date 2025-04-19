package org.firstinspires.ftc.teamcode.OldFiles;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.hardware.lynx.LynxModule;

import java.util.List;

public class ExpansionHub {
    private final List<LynxModule> allHubs; // List of all hubs, includes Control + Expansion

    public ExpansionHub(HardwareMap hardwareMap) {
        allHubs = hardwareMap.getAll(LynxModule.class);

        // Only apply bulk read mode to the Expansion Hub
        for (LynxModule hub : allHubs) {
            if (!hub.isParent()) { // The Expansion Hub is NOT the parent (Control Hub)
                hub.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
            }
        }
    }

    // Clears stale bulk read data once per loop
    public void updateBulkData() {
        for (LynxModule hub : allHubs) {
            if (!hub.isParent()) { // Only clear Expansion Hub cache
                hub.clearBulkCache();
            }
        }
    }
}