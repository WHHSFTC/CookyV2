package org.firstinspires.ftc.teamcode.OldFiles;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Servo", group = "OpModes")
@Config
@Disabled
public class ServoTester extends LinearOpMode {

    // Declare the servos
    private Servo servo1;
    private Servo servo2;
    private Servo servo3;

    public static double pos1 = -1.0, pos2 = 0.0, pos3 = 1.0;

    @Override
    public void runOpMode() {

        servo1 = hardwareMap.get(Servo.class, "servo1");
        servo2 = hardwareMap.get(Servo.class, "servo2");
        servo3 = hardwareMap.get(Servo.class, "servo3");

        waitForStart();

        while (opModeIsActive()) {
            if (gamepad1.dpad_left) {
                servo1.setPosition(pos1);
                servo2.setPosition(pos1);
                servo3.setPosition(pos1);
            } else if (gamepad1.dpad_up) {
                servo1.setPosition(pos2);
                servo2.setPosition(pos2);
                servo3.setPosition(pos2);
            } else if (gamepad1.dpad_right) {
                servo1.setPosition(pos3);
                servo2.setPosition(pos3);
                servo3.setPosition(pos3);
            }


            // Telemetry to show the current servo being controlled and its position
            telemetry.addData("Servo 1 Position", servo1.getPosition());
            telemetry.addData("Servo 2 Position", servo2.getPosition());
            telemetry.addData("Servo 3 Position", servo3.getPosition());
            telemetry.update();
        }
    }
}