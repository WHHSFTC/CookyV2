package org.firstinspires.ftc.teamcode.OpModes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.Subsystems.Mecanum;
import org.firstinspires.ftc.teamcode.Subsystems.ExpansionHub;

@TeleOp(name = "Mecanum Only TeleOp", group = "OpModes")
public class MecOnly extends LinearOpMode {
    private Mecanum mecanum;
    private boolean turtleMode = false; // Tracks turtle mode state, false is regular speed, true means slow speed
    private ExpansionHub expansionHub;

    @Override
    public void runOpMode() {
        // Initialize subsystems
        mecanum = new Mecanum(hardwareMap);

        // Initialize the Expansion Hub
        expansionHub = new ExpansionHub(hardwareMap);

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
                gamepad1.rumbleBlips(turtleMode ? 2 : 1); // 2 blips for turtle mode (slow), 1 blip for fast mode
                sleep(200); // Prevent accidental double toggle
            }

            // Telemetry for debugging
            telemetry.addData("Drive", "y: %.2f, x: %.2f, rot: %.2f", y, x, rotation);
            telemetry.addData("Turtle Mode", mecanum.isTurtleMode());
            telemetry.update();
        }
    }
}
