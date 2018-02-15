/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team7096.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import java.lang.Math.*;

public class Robot extends IterativeRobot {
	DifferentialDrive robotDrive
			= new DifferentialDrive(new Spark(0), new Spark(1));
	
	Joystick operator = new Joystick(0);
	Joystick joystick_L = new Joystick(1);
	Joystick joystick_R = new Joystick(2);
	Joystick gamepad = new Joystick(3);
	
	Servo servo = new Servo(2);
	TalonSRX panTalon = new TalonSRX(0);
	Encoder enc = new Encoder(0,1);
	Timer timer = new Timer();
	
	double vMult = 1.0;
	double a = 0;
	
	boolean pov = false;
	
	boolean previousButton = false;
	boolean currentButton = false;
	boolean vHalf = false;
	
	@Override
	public void robotInit() {
		CameraServer.getInstance().startAutomaticCapture(0);
		CameraServer.getInstance().startAutomaticCapture(1);
	}
	
	@Override
	public void autonomousInit() {
		timer.reset();
		timer.start();
	}
	
	@Override
	public void autonomousPeriodic() {
		
		if (timer.get() < 1.0) {
			robotDrive.arcadeDrive(0.4, 0);
		}
		else if(timer.get() < 3.0) {
			robotDrive.arcadeDrive(0, 0.4);
		}
		else if(timer.get() < 5.0) {
			robotDrive.arcadeDrive(0, -0.4);
		}
		else {
			robotDrive.stopMotor();
		}
	}
		
	@Override
	public void teleopInit() {
		enc.reset();
		vMult = 1.0;
		robotDrive.stopMotor();
		
		previousButton = false;
		currentButton = false;
		vHalf = false;
		
		a = 1.0; //constant (0 to 1)
	}

	boolean tankDriveMode = true;
	
	public double joyMod(double x) {
		double xP = 0;
		if(Math.abs(x) < 0.01) {
			x = (int)x;
			x = (double)x;
		}
		if(x<0) {
			x = Math.abs(x);
			xP = a*(Math.pow(x, (1.0/3.0))) + (1-a)*x;
			xP = -xP;
		}
		else {
			xP = a*(Math.pow(x, (1.0/3.0))) + (1-a)*x;
		}
		return xP;
	}
	
	@Override
	public void teleopPeriodic() {
		
		previousButton = currentButton;
		currentButton = operator.getRawButton(10);

		if (currentButton && !previousButton) 
		{
			vHalf = !vHalf;
			
			if(vHalf) {
				a = 1.0;
				System.out.println("toggle_1");
			}
			else {
				a = 1.0;
				System.out.println("toggle_2");
			}
		}
		
		//Drive Mode Set
		if (joystick_L.getRawButton(2) || gamepad.getRawButton(2)) {
			tankDriveMode = !tankDriveMode;
			System.out.println("Tank Drive" + tankDriveMode);
		}
		//Arcade Drive
		if (!tankDriveMode) {
			robotDrive.arcadeDrive(joystick_L.getY(), joystick_L.getX());
		}
		//Tank Drive
		else {
			robotDrive.tankDrive(gamepad.getRawAxis(1), gamepad.getRawAxis(5));
			
			double R = joyMod(joystick_R.getRawAxis(1));
			double L = joyMod(joystick_L.getRawAxis(1));
			System.out.println(R + " " + L);
			robotDrive.tankDrive(R, L);
		}
		
		//prints
		if (!tankDriveMode && joystick_L.getRawButton(5)) {
			System.out.println(joystick_L.getPOV());
		}
		else if (tankDriveMode && gamepad.getRawButton(5)) {
			System.out.println(gamepad.getPOV());
		}
		else if (!tankDriveMode && joystick_L.getRawButton(3)) {
			System.out.println(joystick_L.getPort());
		}
		
		else if (tankDriveMode && gamepad.getRawButton(3)) {
			System.out.println(gamepad.getPort());
		}
	}
	
	@Override
	public void testPeriodic() {
		
	}
}
