/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class Robot extends TimedRobot {
  private DifferentialDrive my_DDrive;

  private Joystick my_joyStick;

  // CAN bus ID of motor
  private static final int leftMasterID = 1;
  private static final int leftSlaveID = 2;
  private static final int rightMasterID = 3;
  private static final int rightSlaveID = 4;
  // 4 Neo motor
  private CANSparkMax my_leftMaster;
  private CANSparkMax my_leftSlave;
  private CANSparkMax my_rightMaster;
  private CANSparkMax my_rightSlave;

  @Override
  public void robotInit() {
    /**
     * SPARK MAX controllers are intialized over CAN by constructing a CANSparkMax
     * object. The CAN ID, which can be configured using the SPARK MAX Client,
     * is passed as the first parameter.
     * 
     * The Motor Type is passed as the second parameter. Motor type can either be:
     * com.revrobotics.CANSparkMaxLowLevel.MotorType.kBrushless <--Neos
     * com.revrobotics.CANSparkMaxLowLevel.MotorType.kBrushed
     * 
     * This initializes two brushless Masters (Neos) with CAN IDs __
     * and 2 Slave followers (Neos). Change IDs for your setup.
     */
    my_leftMaster = new CANSparkMax(leftMasterID, MotorType.kBrushless);
    my_leftSlave = new CANSparkMax(leftSlaveID, MotorType.kBrushless);
    my_rightMaster = new CANSparkMax(rightMasterID, MotorType.kBrushless);
    my_rightSlave = new CANSparkMax(rightSlaveID, MotorType.kBrushless);

    /**
     * The RestoreFactoryDefaults method can be used to reset the configuration
     * parameters of the SPARK MAX to their factory default state. If no argument
     * is passed, these parameters will not persist between power cycles
     */
    my_leftMaster.restoreFactoryDefaults();
    my_leftSlave.restoreFactoryDefaults();
    my_rightMaster.restoreFactoryDefaults();
    my_rightSlave.restoreFactoryDefaults();

    // invert R side ?
    my_leftMaster.setInverted(false);
    my_rightMaster.setInverted(true);

    // slaves follow
    my_leftSlave.follow(my_leftMaster);
    my_rightSlave.follow(my_rightMaster);

    my_DDrive = new DifferentialDrive(my_leftMaster, my_rightMaster);

    my_joyStick = new Joystick(0);
  } // end robotInit

  // on XboxController these map L knob Y axis to Fwd/Back
  // and R knob X axis to turn <-- check this mapping w/ DS
  @Override
  public void teleopPeriodic() {
    double power = -(my_joyStick.getRawAxis(1));
    double turn = (my_joyStick.getRawAxis(4));
    // deadband
    if (Math.abs(power) < 0.05)
      power = 0;
    if (Math.abs(turn) < 0.05)
      turn = 0;

    my_DDrive.arcadeDrive(power * 0.6, turn * 0.3);
  } // end teleoPeriod
} // end Robot.j
