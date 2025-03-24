package org.firstinspires.ftc.teamcode.OpModes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import org.firstinspires.ftc.teamcode.Subsystems.Mecanum;
import org.firstinspires.ftc.teamcode.Subsystems.Claw;
import org.firstinspires.ftc.teamcode.Subsystems.IntakeArm;

public class InitOpMode extends OpMode {
    private Mecanum mecanum;
    private Claw claw;
    private IntakeArm intakeArm; // Declare the IntakeArm subsystem

    // This method runs once when the "INIT" button is pressed on Driver Station
    @Override
    public void init() {
        // Initialize subsystems
        mecanum = new Mecanum(hardwareMap);
        claw = new Claw(hardwareMap, "clawServo");
        intakeArm = new IntakeArm(hardwareMap, "leftIntakeServo", "rightIntakeServo", "wristServo"); // Initialize IntakeArm

        // Set initial positions and configurations for Claw
        claw.setPosition2(1.0); // Default open position
        claw.setSpeed(0.5); // Smooth movement speed
        claw.closeClaw(); // Ensure the claw starts closed

        // Set initial positions for IntakeArm
        intakeArm.setTransferPosition(1.0); // Transfer position for the intake arm
        intakeArm.moveToTransfer(); // Move arm to transfer position
        intakeArm.moveWrist(0.5); // Set wrist to center position

        // Telemetry updates
        telemetry.addData("Status", "Robot Initialized!");
        telemetry.addData("Claw Position", claw.getServoPosition());
        telemetry.addData("Intake Arm Position", intakeArm.getLeftServoPosition()); // Display position of intake arm
        telemetry.addData("Wrist Position", intakeArm.getWristServoPosition()); // Display wrist position
        telemetry.update();
    }

    // This method is called repeatedly during the match, but here it's not doing anything
    @Override
    public void loop() {
        // No continuous logic is needed here for initialization
    }
}
