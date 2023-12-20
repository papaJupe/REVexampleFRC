// closeLooPosCtlCmdAuto            GoToPosition Cmd  --- aka GTP cmd

package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class GoToPosition extends CommandBase {// for Spark/Neo use 
  // for elevator, would need conversion factor from encoder reading to
  // inch of travel; encodInstance.setPositionConversionFactor(inch travel/
  // one motor rot) -- so encoder output is read in inches traveled
  private double inchTarget;
  private double leftEnco;

// CONSTRUCTOR
  public GoToPosition(Double targetPos) {
    inchTarget = targetPos;
    // Use addRequirements() here to declare subsystem dependency
    addRequirements(Robot.myDrive);
  } // end constructor

  // Called just before this Cmd runs first time (each button press?);
  // maybe just first run on code deploy? data slot 0-3, pid loop 0-1
  @Override // need to zero encoder; PID slot likely already set
  public void initialize() {
    // Robot._motorSubsys.leftMaster.selectProfileSlot(Robot._motorSubsys
    // ._position_slot, 0);
    // Robot._motorSubsys.rightMaster.selectProfileSlot(Robot._motorSubsys
    // ._position_slot, 0);
    Robot.myDrive.zeroEncoder();
    leftEnco = 0; // var used in isFin(), ? if reset on cmd reuse here 
    SmartDashboard.putString("GTPcmd fin?", "1st init");  
 }

  // subsyst method called repeatedly when this Cmd is scheduled
  // inch param sent to subsys
  @Override
  public void execute() { // is this seen on SmtDash ever?
    SmartDashboard.putString("GTPcmd fin?", "exec");
    Robot.myDrive.goStraightPosition(inchTarget);
  }

  // ... returns true when this Command no longer needs to run execute();
  // here, when target pos reached
  @Override
  public boolean isFinished() { // must finish for sequence it's in to advance
    // need to see value to tell how close to target w/ present pid #'s
    leftEnco = DriveSubsys.leftEncoder.getPosition(); // now OK
    // keeping printout current on repeat calls to teleop and auto

    boolean atTargetPos = (Math.abs(leftEnco) + 1 >= (Math.abs(inchTarget)));
    System.out.println("target is " + inchTarget);
    System.out.println("leftEnco  = " + leftEnco);


    if (atTargetPos) {
      SmartDashboard.putString("GTPcmd fin?", "true");
     // leftEnco = 0;  // if here, causes cmd repeat before exiting
      return true;
    } else {
      SmartDashboard.putString("GTPcmd fin?", "not fin");
      return false;
    }
 } // end isFinished

  // Called once after isFinished returns true
  @Override
  public void end(boolean endme) { // tidy up
    SmartDashboard.putString("GTPcmd fin?", "ended");    
     leftEnco = 0;
    System.out.println("GTP says Done!");
  }
} // end GTP class
