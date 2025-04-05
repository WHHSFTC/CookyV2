package org.firstinspires.ftc.teamcode.OpModes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Subsystems.Mecanum;
import org.firstinspires.ftc.teamcode.Subsystems.Claw;
import org.firstinspires.ftc.teamcode.Subsystems.IntakeArm;
import org.firstinspires.ftc.teamcode.Subsystems.ExpansionHub;
import com.acmerobotics.dashboard.config.Config;

@TeleOp(name = "Mecanum with Claw", group = "OpModes")
@Config
public class DualDriverOpMode extends LinearOpMode {
    private Mecanum mecanum;
    private Claw claw;
    private IntakeArm intakeArm;
    private boolean turtleMode = false; // Tracks turtle mode state, false is regular speed, true means slow speed
    private ExpansionHub expansionHub;
    public static double wristL = .18, wristR = .82;

    @Override
    public void runOpMode() {
        // Initialize subsystems
        mecanum = new Mecanum(hardwareMap);
        claw = new Claw(hardwareMap, "clawServo");
        intakeArm = new IntakeArm(hardwareMap, "leftIntakeServo", "rightIntakeServo", "wristServo");

        // Initialize the Expansion Hub
        expansionHub = new ExpansionHub(hardwareMap);

        // Set initial intake arm positions
        intakeArm.setTransferPosition(1.0); // Transfer position for the intake arm
        intakeArm.moveToTransfer(); // Move arm to transfer position
        intakeArm.moveWrist(0.5); // Set wrist to center position

        waitForStart();

        while (opModeIsActive()) {
            // Update bulk data for the Expansion Hub
            expansionHub.updateBulkData();

            // Gamepad 1: Driving
            double y = -gamepad1.left_stick_y; // Forward/backward
            double x = gamepad1.left_stick_x;  // Strafe left/right
            double rotation = gamepad1.right_stick_x; // Rotation

            mecanum.drive(x, y, rotation);

            // Toggle Turtle Mode when pressing Right Bumper
            if (gamepad1.right_bumper) {
                turtleMode = !turtleMode; // Flip state
                mecanum.setTurtleMode(turtleMode);

                // Provide rumble feedback for turtle mode
                if (turtleMode) {
                    gamepad1.rumbleBlips(2); // 2 blips for turtle mode (slow)
                } else {
                    gamepad1.rumbleBlips(1); // 1 blip for fast mode
                }
                //sleep(200); // Prevent accidental double toggle
            }

            // Gamepad 2: Claw control
            if (gamepad2.a) {
                claw.openClaw();
            } else if (gamepad2.b) {
                claw.closeClaw();
            }

            // Gamepad 2: Intake Arm control
            if (gamepad2.x) {
                intakeArm.moveToIntake(); // Move intake arm to intake position
            } else if (gamepad2.y) {
                intakeArm.moveToTransfer(); // Move intake arm to transfer position
            }

            // Move wrist using the dpad
            if (gamepad2.dpad_left) {
                intakeArm.moveWrist(wristL); // Move wrist to left position
            } else if (gamepad2.dpad_right) {
                intakeArm.moveWrist(wristR); // Move wrist to right position
            } else if (gamepad2.dpad_up) {
                intakeArm.moveWrist(0.5);
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
}
