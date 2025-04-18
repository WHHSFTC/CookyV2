package org.firstinspires.ftc.teamcode.OpModes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.Subsystems.ExpansionHub;
import org.firstinspires.ftc.teamcode.Subsystems.Mecanum;
import org.firstinspires.ftc.teamcode.Subsystems.Claw;
import org.firstinspires.ftc.teamcode.Subsystems.IntakeArm;
import org.firstinspires.ftc.teamcode.Subsystems.Slides;

public class InitOpMode extends OpMode {
    protected Mecanum mecanum;
    protected Claw claw;
    protected IntakeArm intakeArm;
    protected Slides horizontalSLides;
    protected ExpansionHub expansionHub;

    // This method runs once when the "INIT" button is pressed on Driver Station
    @Override
    public void init() {
        mecanum = new Mecanum(hardwareMap);
        claw = new Claw(hardwareMap, "clawServo");
        intakeArm = new IntakeArm(hardwareMap, "leftIntakeServo", "rightIntakeServo", "wristServo");
        horizontalSLides = new Slides(hardwareMap, "horizontalSlideMotor");
        claw = new Claw(hardwareMap, "clawServo");
        intakeArm = new IntakeArm(hardwareMap, "leftIntakeServo", "rightIntakeServo", "wristServo"); // Initialize IntakeArm

        expansionHub.updateBulkData();

        // Set initial positions and configurations for Claw
        claw.openClaw(); // Ensure the claw starts closed

        // Set initial positions for IntakeArm
        intakeArm.moveToTransfer(); // Move arm to transfer position
        intakeArm.wristCenterPos(); // Set wrist to center position

        // Telemetry updates
        telemetry.addData("Status", "Robot Initialized!");
        telemetry.addData("Claw Position", claw.getServoPosition());
        telemetry.addData("Intake Arm Position", intakeArm.getLeftServoPosition()); // Display position of intake arm
        telemetry.addData("Wrist Position", intakeArm.getWristServoPosition()); // Display wrist position
        telemetry.update();
    }

    // This method is called repeatedly during the match, but here it's not doing anything
    public void loop() {
        // No continuous logic is needed here for initialization
    }
}
