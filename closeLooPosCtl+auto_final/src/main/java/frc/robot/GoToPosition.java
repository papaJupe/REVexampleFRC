//closeLooPosition+auto  GoToPosition Cmd  --- aka GTP cmd

package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class GoToPosition extends CommandBase {
 
  private double _inchTarget;
  private double leftEnco;

// CONSTRUCTOR
  public GoToPosition(Double targetPos) {

    _inchTarget = targetPos;
    // Use addRequirements() here to declare subsystem dependency
    addRequirements(Robot._myDrive);

  }

  // Called just before this Cmd runs first time (each button press?);
  // maybe just first run on code deploy? data slot 0-3, pid loop 0-1
  @Override // need to zero encoder; PID slot likely already set
  public void initialize() {
    // Robot._motorSubsys.leftMaster.selectProfileSlot(Robot._motorSubsys
    // ._position_slot, 0);
    // Robot._motorSubsys.rightMaster.selectProfileSlot(Robot._motorSubsys
    // ._position_slot, 0);
    Robot._myDrive.zeroEncoder(0);
    leftEnco = 0; // var used in isFin(), ? if reset on cmd reuse here or there
    SmartDashboard.putString("GTPcmd fin?", "1st init"); // too 

  }

  // subsyst method called repeatedly when this Cmd is scheduled
  // inch param sent to subsys
  @Override
  public void execute() { // is this seen on SD ever?
    SmartDashboard.putString("GTPcmd fin?", "exec");
   // leftEnco = (int) DriveSubsys._leftEncoder.getPosition();
    Robot._myDrive.goStraightPosition(_inchTarget);

  }

  // ... returns true when this Command no longer needs to run execute();
  // here, when target pos reached
  @Override
  public boolean isFinished() { // must finish for sequence it's in to advance
    // need to see value to tell how close to target w/ pid #'s
    leftEnco = DriveSubsys._leftEncoder.getPosition(); // was flaky, now OK
    // to keep printout current on repeat calls to teleop and auto

    boolean _atTargetPos = (Math.abs(leftEnco) + 1 >= (Math.abs(_inchTarget)));
    System.out.println("target is " + _inchTarget);
    System.out.println("leftEnco  = " + leftEnco);

    if (_atTargetPos) {
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
