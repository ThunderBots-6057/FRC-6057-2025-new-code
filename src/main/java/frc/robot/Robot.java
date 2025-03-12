// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;


import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

import java.util.function.BooleanSupplier;
import java.util.function.LongSupplier;

// See https://docs.revrobotics.com/revlib/install#c++-and-java-installation
// For how to install revrobotics library
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkMaxConfig;

public class Robot extends TimedRobot {
  private DifferentialDrive m_robotDrive;
  private Joystick m_driver;
  private Joystick m_operator;

  private final SparkMaxConfig c_leftFrontMotor = new SparkMaxConfig();
  private final SparkMax m_leftFrontMotor = new SparkMax(4, MotorType.kBrushed);
  private final SparkMaxConfig c_rightFrontMotor = new SparkMaxConfig();
  private final SparkMax m_rightFrontMotor = new SparkMax(5, MotorType.kBrushed);
  private final SparkMaxConfig c_leftRearMotor = new SparkMaxConfig();
  private final SparkMax m_leftRearMotor = new SparkMax(51, MotorType.kBrushed);
  private final SparkMaxConfig c_rightRearMotor = new SparkMaxConfig();
  private final SparkMax m_rightRearMotor = new SparkMax(3, MotorType.kBrushed);
  private final SparkMax m_algeIntakeArm = new SparkMax(6, MotorType.kBrushed);
  private final SparkMax m_algeIntake = new SparkMax(7, MotorType.kBrushed);
  private final SparkMax m_coralIntake = new SparkMax(8, MotorType.kBrushed);
  private final SparkMax m_coralIntakeAngle = new SparkMax(9, MotorType.kBrushed);
  private final SparkMax m_climb = new SparkMax(10, MotorType.kBrushed);




  @Override
  public void robotInit() {
    // we need one side of the divetrain so that positive voltages
    // result in both sides moving forward. Depending on how your robot's
    // gearbox is constructed, you might have to invert the left side instead
    c_rightFrontMotor.inverted(true);
    c_rightRearMotor.inverted(true);
    
    //make the rears follow the fronts
    c_leftRearMotor.follow(m_leftFrontMotor);
    c_rightRearMotor.follow(m_rightFrontMotor);

    // Apply the configs
    m_leftFrontMotor.configure(c_leftFrontMotor, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    m_leftRearMotor.configure(c_leftRearMotor, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    m_rightFrontMotor.configure(c_rightFrontMotor, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    m_rightRearMotor.configure(c_rightRearMotor, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    //Set up dive and control systems
    m_robotDrive = new DifferentialDrive(m_leftFrontMotor::set, m_rightFrontMotor::set);
    m_driver = new Joystick(0);
    m_operator = new Joystick(1);
  }
      //Set the neutral mode to brake
      @Override
    public void teleopPeriodic() {
         m_robotDrive.tankDrive(-m_driver.getRawAxis(1)*0.6,m_driver.getRawAxis(5)*0.6);
     

  }}
