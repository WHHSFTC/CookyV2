package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;


@TeleOp(name="Testing servo")
@Disabled

public class MyFIRSTJavaOpMode extends LinearOpMode {
    //private DcMotor motorTest;


    @Override
    public void runOpMode() {
        Servo servoTest = hardwareMap.get(Servo.class, "GoBildaServo");

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            telemetry.addData("Status", "Running");
            telemetry.update();
            double tgtPower = 0;

                tgtPower = -this.gamepad1.left_stick_y;
                //motorTest.setPower(tgtPower);
                // check to see if we need to move the servo.
                if(gamepad1.y) {
                    // move to 0 degrees.
                    servoTest.setPosition(0);
                } else if (gamepad1.x || gamepad1.b) {
                    // move to 90 degrees.
                    servoTest.setPosition(0.5);
                } else if (gamepad1.a) {
                    // move to 180 degrees.
                    servoTest.setPosition(1);
                }
                telemetry.addData("Servo Position", servoTest.getPosition());
                telemetry.addData("Target Power", tgtPower);
                //telemetry.addData("Motor Power", motorTest.getPower());
                telemetry.addData("Status", "Running");
                telemetry.update();


        }
    }
}
