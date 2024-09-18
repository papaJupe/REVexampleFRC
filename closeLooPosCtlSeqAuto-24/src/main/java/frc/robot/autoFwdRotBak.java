// REVexample mod / closeLooPosCtl+SeqAuto     autoFwdRotBak.j

package frc.robot;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class autoFwdRotBak extends SequentialCommandGroup {

    public autoFwdRotBak() {
        // Add your commands in the addCommands() call, e.g.
        // addCommands(new FooCommand(), new BarCommand());
        addCommands(  // comma may work as well as .andThen
                new GoToPosition(Robot.autoDriveInch)
                   .andThen(new WaitCommand(5.0))
                   .andThen(new GoToPosition(-Robot.autoDriveInch))
                   .andThen(new WaitCommand(5.0))
                   .andThen(new GoToPosition(Robot.autoDriveInch - 12)));

        // addCommands(new GoToPosition(Constant.autoDriveInch),
        // new WaitCommand(3.0),
        // new RotateInPlace(14), new WaitCommand(3.0),
        // new GoToPosition(Constant.autoDriveInch - 12));
       
    } // end constructor

}  // end class