package org.firstinspires.ftc.teamcode.OpModes;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.acmerobotics.dashboard.config.Config;

@TeleOp(name = "Single Driver OpMode", group = "OpModes")
@Config
public class SingleDriverOpMode extends InitOpMode {
    private boolean turtleMode = false; // Tracks turtle mode state, false is regular speed, true means slow speed
    public static double kP = 0.01, kI = 0.0, kD = 0.0001;
    public static int extendedPos = 200, retractedPos = 0;

    @Override
    public void loop() {
        // Update bulk data for the Expansion Hub


        double y = -gamepad1.left_stick_y; // Forward/backward
        double x = gamepad1.left_stick_x;  // Strafe left/right
        double rotation = gamepad1.right_stick_x; // Rotation

        mecanum.drive(x, y, rotation);

        // Toggle Turtle Mode when pressing Right Bumper
        if (gamepad1.dpad_down) {
            turtleMode = !turtleMode; // Flip state
            mecanum.setTurtleMode(turtleMode);

            // Provide rumble feedback for turtle mode
            if (turtleMode) {
                gamepad1.rumbleBlips(2); // 2 blips for turtle mode (slow)
            } else {
                gamepad1.rumbleBlips(1); // 1 blip for fast mode
            }
        }

        if (gamepad1.right_bumper && !claw.isOpen()) {
            claw.openClaw();
        } else if (gamepad1.right_bumper && claw.isOpen()) {
            claw.closeClaw();
        }

        if (gamepad1.dpad_right) {
            intakeArm.moveToIntake(); // Move intake arm to intake position
        } else if (gamepad1.dpad_up) {
            intakeArm.moveToHover(); // Move intake arm to Hover position
        } else if (gamepad1.dpad_left) {
            intakeArm.moveToTransfer(); // Move intake arm to transfer position
        }

        // Move wrist using the dpad
        if (gamepad1.left_trigger > 0) {
            intakeArm.wristLeftPos(); // Move wrist to left position
        } else if (gamepad1.right_trigger > 0) {
            intakeArm.wristRightPos(); // Move wrist to right position
        } else if (gamepad1.left_bumper) {
            intakeArm.wristCenterPos(); // Move wrist to center position
        }

        if (gamepad1.a) {
            horizontalSLides.goTo(extendedPos);
        } else if (gamepad1.b) {
            horizontalSLides.goTo(retractedPos);
        }

        // Telemetry for debugging
        telemetry.addData("Drive", "y: %.2f, x: %.2f, rot: %.2f", y, x, rotation);
        telemetry.addData("Turtle Mode", mecanum.isTurtleMode());
        telemetry.addData("Claw Position", claw.getServoPosition());
        telemetry.addData("Intake Arm Position", intakeArm.getLeftServoPosition()); // Display position of intake arm
        telemetry.addData("Wrist Position", intakeArm.getWristServoPosition()); // Display wrist position
        telemetry.update();

    }
}
