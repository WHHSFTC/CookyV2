package org.firstinspires.ftc.teamcode.OpModes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.Subsystems.Mecanum;
import org.firstinspires.ftc.teamcode.Subsystems.Claw;

public class InitOpMode extends OpMode {
    private Mecanum mecanum;
    private Claw claw;

    // This method runs once when the "INIT" button is pressed on Driver Station
    @Override
    public void init() {
        // Initialize subsystems
        mecanum = new Mecanum(hardwareMap);
        claw = new Claw(hardwareMap, "clawServo");

        // Set initial positions and configurations
        claw.setPosition2(1.0); // Default open position
        claw.setSpeed(0.5); // Smooth movement speed
        claw.closeClaw(); // Ensure the claw starts closed

        telemetry.addData("Status", "Robot Initialized!");
        telemetry.addData("Claw Position", claw.getServoPosition());
        telemetry.update();
    }

    // This method is called repeatedly during the match, but here it's not doing anything
    @Override
    public void loop() {
        // No continuous logic is needed here for initialization
    }
}
