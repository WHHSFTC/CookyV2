package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

@TeleOp
public class SensorLimelight3A extends LinearOpMode {

    private Limelight3A limelight;
    private NetworkTable limelightTable;
    private double tx, ty, ta, tv;
    private double targetAlignThreshold = 1.0;
    private double moveSpeed = 0.5;

    private DcMotor leftFrontDrive, rightFrontDrive, leftBackDrive, rightBackDrive;

    @Override
    public void runOpMode() throws InterruptedException {

        // Wait for the game to start and establish the connection
        telemetry.addData(">", "Robot Ready. Waiting for Limelight...");
        telemetry.update();

        // Initialize NetworkTable and Limelight
        limelightTable = NetworkTableInstance.getDefault().getTable("limelight");
        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        // Initialize motors
        leftFrontDrive = hardwareMap.get(DcMotor.class, "frontLeft");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "frontRight");
        leftBackDrive = hardwareMap.get(DcMotor.class, "backLeft");
        rightBackDrive = hardwareMap.get(DcMotor.class, "backRight");

        // Set motor directions
        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        leftBackDrive.setDirection(DcMotor.Direction.REVERSE);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        rightBackDrive.setDirection(DcMotor.Direction.FORWARD);

        // Wait for the start button to be pressed
        waitForStart();

        // Limelight initialization status check
        if (opModeIsActive()) {
            try {
                limelight.start();  // Start the Limelight camera
                telemetry.addData("Limelight", "Started Successfully");
            } catch (Exception e) {
                telemetry.addData("Error", "Failed to start Limelight: " + e.getMessage());
                telemetry.update();
                return;
            }
        }

        // Main loop
        while (opModeIsActive()) {
            // Ensure Limelight networkTable is active
            try {
                // Fetch latest data from Limelight
                tx = limelightTable.getEntry("tx").getDouble(0.0);
                ty = limelightTable.getEntry("ty").getDouble(0.0);
                ta = limelightTable.getEntry("ta").getDouble(0.0);
                tv = limelightTable.getEntry("tv").getDouble(0.0);
            } catch (Exception e) {
                telemetry.addData("Error", "NetworkTable error: " + e.getMessage());
                telemetry.update();
                continue;
            }

            telemetry.addData("TX", tx);
            telemetry.addData("TY", ty);
            telemetry.addData("TA", ta);
            telemetry.addData("TV", tv);

            if (gamepad1.right_trigger > 0.5) {
                // Align with target if visible
                if (tv == 1.0) {
                    alignWithTarget();
                } else {
                    stopRobot();
                    telemetry.addData("Error", "No target found");
                }
            } else {
                manualDrive(); // Use manual drive controls
            }

            telemetry.update(); // Update telemetry to the driver hub
        }

        // Stop Limelight after opmode ends
        limelight.stop();
    }

    private void alignWithTarget() {
        if (tx > targetAlignThreshold) {
            // Target is to the right, turn right
            robotDrive(-moveSpeed, moveSpeed);
        } else if (tx < -targetAlignThreshold) {
            // Target is to the left, turn left
            robotDrive(moveSpeed, -moveSpeed);
        } else {
            // Target is centered, move forward
            robotDrive(moveSpeed, moveSpeed);
        }
    }

    private void manualDrive() {
        // Manual control of the robot using the left joystick
        double drive = -gamepad1.left_stick_y;  // Forward/reverse control
        double strafe = gamepad1.left_stick_x; // Turn control
        robotDrive(drive, strafe);
    }

    private void robotDrive(double leftPower, double rightPower) {
        // Drive motors with power values (normalized to be between -1 and 1)
        double yaw = gamepad1.right_stick_x;

        double leftFrontPower = leftPower - rightPower - yaw;
        double rightFrontPower = leftPower + rightPower + yaw;
        double leftBackPower = leftPower + rightPower - yaw;
        double rightBackPower = leftPower - rightPower + yaw;

        // Normalize motor power values to prevent over-driving
        double max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
        max = Math.max(max, Math.abs(leftBackPower));
        max = Math.max(max, Math.abs(rightBackPower));

        if (max > 1.0) {
            leftFrontPower /= max;
            rightFrontPower /= max;
            leftBackPower /= max;
            rightBackPower /= max;
        }

        leftFrontDrive.setPower(leftFrontPower);
        rightFrontDrive.setPower(rightFrontPower);
        leftBackDrive.setPower(leftBackPower);
        rightBackDrive.setPower(rightBackPower);
    }

    private void stopRobot() {
        // Stop all motors
        robotDrive(0, 0);
    }
}
