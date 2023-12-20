//  closeLooPosCtlSeqAuto     GoToPosition Cmd  --- aka GTP cmd

package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;

// import frc.robot.Robot.*;
// import frc.robot.DriveSubsys.*;

public class GoToPosition extends CommandBase {

  private double inchTarget;
  private double leftEnco;
  private int loopCount = 0; // for printing var

  // CONSTRUCTOR Creates a new GoTo(target)Position cmd
  public GoToPosition(double targetPos) {

    inchTarget = targetPos;
    // Use addRequirements() here to declare subsystem dependencies
    addRequirements(Robot.myDrive);

  }

  // Called just before this Cmd runs first time only (each deploy/restart?);
  // PID param sets reside in data slot 0-3, pid loop 0-1 allow alt config
  @Override // need to zero encoder; PID slot already set in subsys
  public void initialize() {
    // Robot.motorSubsys.leftMaster.selectProfileSlot(Robot.motorSubsys
    // .position_slot, 0);
    // Robot.motorSubsys.rightMaster.selectProfileSlot(Robot.motorSubsys
    // .position_slot, 0);
    Robot.myDrive.zeroEncoder(0);
    leftEnco = 0; // var used in isFin(), ? if reset on cmd reuse here or there
    SmartDashboard.putString("GTPcmd fin?", "1st init"); // too

  }

  // subsyst method called repeatedly when this Cmd is scheduled
  // inch param sent to subsys
  @Override
  public void execute() { // is this seen on SmtDbd ever? probably transient
    SmartDashboard.putString("GTPcmd fin?", "exec");
    // leftEnco = DriveSubsys.leftEncoder.getPosition();
    Robot.myDrive.goStraightPosition(inchTarget);

  }

  // ... returns true when this Command no longer needs to run execute();
  // here, when target position reached
  @Override
  public boolean isFinished() { //must finish for sequence it's in to advance
    leftEnco = DriveSubsys.leftEncoder.getPosition(); // now OK
    // to keep printout current on repeat calls in teleop and auto
    if (loopCount++ >= 50) {
      System.out.println("target is " + inchTarget);
      System.out.println("leftEnco  = " + leftEnco);
      loopCount = 0;
    }

    boolean atTargetPos = (Math.abs(leftEnco) + 1 >= (Math.abs(inchTarget)));

    if (atTargetPos) {
      SmartDashboard.putString("GTPcmd fin?", "true");
      // leftEnco = 0; // if here, causes cmd repeat before exiting
      return true;
    } else {
      SmartDashboard.putString("GTPcmd fin?", "not fin");
      return false;
    }
  } // end isFinished

  // Called once after isFinished returns true
  @Override
  public void end(boolean endme) { 
    SmartDashboard.putString("GTPcmd fin?", "ended");
    leftEnco = 0;
    System.out.println("GTP says endme!");
  }  // end end

} // end GTP class

