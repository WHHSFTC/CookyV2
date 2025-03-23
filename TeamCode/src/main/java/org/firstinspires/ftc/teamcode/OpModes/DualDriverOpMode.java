package org.firstinspires.ftc.teamcode.OpModes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Subsystems.Mecanum;
import org.firstinspires.ftc.teamcode.Subsystems.Claw;

@TeleOp(name = "Mecanum + Claw TeleOp", group = "OpModes")
public class DualDriverOpMode extends LinearOpMode {
    private Mecanum mecanum;
    private Claw claw;
    private boolean turtleModeToggle = false; // Tracks turtle mode state

    @Override
    public void runOpMode() {
        // Initialize subsystems
        mecanum = new Mecanum(hardwareMap);
        claw = new Claw(hardwareMap, "clawServo");

        // Set claw's open and close positions
        claw.setPosition1(0.2);
        claw.setPosition2(0.8);
        claw.setSpeed(0.5);

        waitForStart();

        while (opModeIsActive()) {
            // Gamepad 1: Driving
            double y = -gamepad1.left_stick_y; // Forward/backward
            double x = gamepad1.left_stick_x;  // Strafe left/right
            double rotation = gamepad1.right_stick_x; // Rotation

            mecanum.drive(x, y, rotation);

            // Toggle Turtle Mode when pressing Left Bumper
            if (gamepad1.left_bumper) {
                turtleModeToggle = !turtleModeToggle; // Flip state
                mecanum.setTurtleMode(turtleModeToggle);
                sleep(200); // Prevent accidental double toggle
            }

            // Gamepad 2: Claw control
            if (gamepad2.a) {
                claw.openClaw();
            } else if (gamepad2.b) {
                claw.closeClaw();
            }

            // Telemetry for debugging
            telemetry.addData("Drive", "y: %.2f, x: %.2f, rot: %.2f", y, x, rotation);
            telemetry.addData("Turtle Mode", mecanum.isTurtleMode());
            telemetry.addData("Claw Position", claw.getServoPosition());
            telemetry.update();
        }
    }
}