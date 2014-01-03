package search.crawl.napoli.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CommandExecute {
	private CommandExecute() {
	}
	public static CommandExecuteUtil build(String workingDirectory) {
		return new CommandExecuteUtil(workingDirectory);
	}
	public static CommandExecuteUtil build() {
		return new CommandExecuteUtil();
	}
	public static class CommandExecuteUtil {
		/* logger 생성 */
		static final Logger LOGGER = LoggerFactory.getLogger(CommandExecuteUtil.class);

		private String workingDirectory = System.getProperty("user.dir");
		public CommandExecuteUtil() {
			super();
		}
		public CommandExecuteUtil(String dir) { 
			super();
			this.workingDirectory = dir;
		}
		public void setWorkingDirectory(String dir) { 
			this.workingDirectory = dir;
		}
		
		public String execute(String... command) { 
			ProcessBuilder builder = new ProcessBuilder(command);
			builder.directory(new File(this.workingDirectory));
			StringBuilder sb = new StringBuilder();
			Process process = null;
			try { 
				//System.out.println("Command : " + builder.command().toString());
				process = builder.start();
				BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
				int wi = process.waitFor();
				
				if (wi != 0) {
					LOGGER.error("Command Execute Fail : << " + wi + " >>");
					LOGGER.error("Command : " + builder.command().toString());
				}
				
				String line = "";
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
			} catch (IOException ie) { 
				LOGGER.error("Command : " + builder.command().toString() + " >> IOException : " + ie.toString());
				throw new RuntimeException(ie);
			} catch (InterruptedException iee) { 
				LOGGER.error("Command : " + builder.command().toString() + " >> InterruptedException : " + iee.toString());
				throw new RuntimeException(iee);
			} finally { 
				if (process != null) { 
					try { 
						process.getInputStream().close();
						process.getOutputStream().close();
						process.getErrorStream().close();
					} catch (IOException e) {
						//ignore.
						LOGGER.debug("process open error.");
					}
				} else { 
					LOGGER.error("Command : " + builder.command().toString() + " >> process is null ");
					throw new RuntimeException("Can't close");
				}
			}
			return sb.toString();
		}
	}
}

