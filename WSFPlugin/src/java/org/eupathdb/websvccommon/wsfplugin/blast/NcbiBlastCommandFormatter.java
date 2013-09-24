package org.eupathdb.websvccommon.wsfplugin.blast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.gusdb.wsf.plugin.WsfPluginException;

public abstract class NcbiBlastCommandFormatter implements CommandFormatter {

  private static final Logger logger = Logger.getLogger(NcbiBlastCommandFormatter.class);

  protected BlastConfig config;

  public abstract String getBlastDatabase(Map<String, String> params);

  @Override
  public void setConfig(BlastConfig config) {
    this.config = config;
  }

  @Override
  public String[] formatCommand(Map<String, String> params, File seqFile,
      File outFile) throws IOException, WsfPluginException {
    // now prepare the commandline
    List<String> cmds = new ArrayList<String>();
    cmds.add(config.getBlastPath() + "blastall");

    // get the algorithm
    String blastApp = params.remove(AbstractBlastPlugin.PARAM_ALGORITHM);
    cmds.add("-p");
    cmds.add(blastApp);

    // get the blast database
    String blastDbs = getBlastDatabase(params);
    cmds.add("-d");
    cmds.add(blastDbs);

    // add the input and output file
    cmds.add("-i");
    cmds.add(seqFile.getAbsolutePath());
    cmds.add("-o");
    cmds.add(outFile.getAbsolutePath());

    for (String paramName : params.keySet()) {
      if (paramName.equals(AbstractBlastPlugin.PARAM_EVALUE)) {
        cmds.add("-e");
        cmds.add(params.get(paramName));
      } else if (paramName.equals(AbstractBlastPlugin.PARAM_MAX_ALIGNMENTS)) {
        String alignments = params.get(paramName);
        cmds.add("-b");
        cmds.add(alignments);
        cmds.add("-v");
        cmds.add(alignments);
      } else if (paramName.equals(AbstractBlastPlugin.PARAM_FILTER)) {
        cmds.add("-F");
        cmds.add(params.get(paramName).equals("yes") ? "m S" : "F");
      }
    }

    cmds.add("-p");
    cmds.add(blastApp);
    cmds.add("-d");
    cmds.add(blastDbs);
    cmds.add("-i");
    cmds.add(seqFile.getAbsolutePath());
    cmds.add("-o");
    cmds.add(outFile.getAbsolutePath());
    logger.debug(blastApp);// + " inferred from (" + qType + ", " + dbType
    // + ")");

    String[] cmdArray = new String[cmds.size()];
    cmds.toArray(cmdArray);
    return cmdArray;
  }

}