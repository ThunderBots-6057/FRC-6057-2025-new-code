// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;


import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj2.command.button.POVButton;

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
  private final SparkMax m_algeIntakeArm = new SparkMax(7, MotorType.kBrushed);
  private final SparkMax m_algeIntake = new SparkMax(8, MotorType.kBrushed);
  private final SparkMax m_coralIntake = new SparkMax(13, MotorType.kBrushed);
  private final SparkMax m_coralIntakeAngle = new SparkMax(2, MotorType.kBrushed);
  private final SparkMax m_climb = new SparkMax(15, MotorType.kBrushed);

  private boolean b_driveSpeed = true;



  // pnuematics to be added
  // Operator r stick up n down







  @Override
  public void robotInit() {


    // we need one side of the divetrain so that positive voltages
    // result in both sides moving forward. Depending on how your robot's
    // gearbox is constructed, you might have to invert the left side instead
    c_rightFrontMotor.inverted(true);
    c_rightRearMotor.inverted(true);
    
    c_leftFrontMotor.inverted(true);
    c_leftRearMotor.inverted(true);
    
    //make the rears follow the fronts
    c_leftRearMotor.follow(m_leftFrontMotor);
    c_rightRearMotor.follow(m_rightFrontMotor);

    // Apply the configs
    m_leftFrontMotor.configure(c_leftFrontMotor, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    m_leftRearMotor.configure(c_leftRearMotor, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    m_rightFrontMotor.configure(c_rightFrontMotor, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    m_rightRearMotor.configure(c_rightRearMotor, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    //Set up drive and control systems
    //Add drive speed toggle
    m_robotDrive = new DifferentialDrive(m_leftFrontMotor::set, m_rightFrontMotor::set);
    m_driver = new Joystick(0);
    m_operator = new Joystick(1);

    
  }
      //Set the neutral mode to brake
      @Override
    public void teleopPeriodic() {

      if (m_driver.getRawButton(0)){
        b_driveSpeed = !b_driveSpeed;
     }

     if(b_driveSpeed){
       m_robotDrive.tankDrive(-m_driver.getRawAxis(1),m_driver.getRawAxis(5));
     }else {
       m_robotDrive.tankDrive(-m_driver.getRawAxis(1)*0.6,m_driver.getRawAxis(5)*0.6);
     }

      

      //Algae Intake Arm
      if (m_operator.getPOV(1) == 0){
       m_algeIntakeArm.set(1);
      } else if (m_operator.getPOV(1) == 180) {
       m_algeIntakeArm.set(-1);
      } else {
       m_algeIntakeArm.set(0);
      }

      //Algae Intake
      if(m_operator.getRawAxis(3) >= 0.5 ){
        m_algeIntake.set(1);
      } else if (m_operator.getRawAxis(2) >= 0.5){
       m_algeIntake.set(-1);
      } else {
       m_algeIntake.set(0);
      }

      //Coral Intake
      if(m_operator.getRawButton(6)){
        m_coralIntake.set(1);
      } else if (m_operator.getRawButton(5)){
        m_coralIntake.set(-1);
      } else {
       m_coralIntake.set(0);
      }
      
      //Coral Arm
      if (m_operator.getRawAxis(1) > 0.1){
       m_algeIntakeArm.set(-1);
      } else if(m_operator.getRawAxis(1) < -0.1){
       m_algeIntakeArm.set(1);
      } else {
       m_algeIntakeArm.set(0);
      }

      //Coral Arm Elevator

      //Climber Elevator
      if (m_driver.getRawAxis(3) >= 0.5){
       m_climb.set(1);
      } else if (m_driver.getRawAxis(2) >= 0.5){
       m_climb.set(-1);
      } else {
       m_climb.set(0);
      }

      
    }}
