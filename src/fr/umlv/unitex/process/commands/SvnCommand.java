/*
 * Unitex
 *
 * Copyright (C) 2001-2012 Université Paris-Est Marne-la-Vallée <unitex@univ-mlv.fr>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA.
 *
 */
package fr.umlv.unitex.process.commands;

import java.io.File;

import fr.umlv.unitex.config.Config;
import fr.umlv.unitex.config.ConfigManager;

/**
 * @author Sébastien Paumier
 * 
 */
public class SvnCommand extends CommandBuilder {
	
	public SvnCommand() {
		super(false);
		element("java");
		element("-jar");
		protectElement(new File(ConfigManager.getManager().getApplicationDirectory(), "svnkitclient.jar")
				.getAbsolutePath());
		element("--non-interactive");
		element("--trust-server-cert");
	}

	
	public SvnCommand auth(String login,String passwd) {
		if (login!=null) {
			element("--username");
			protectElement(login);
		}
		if (passwd!=null) {
			element("--password");
			protectElement(passwd);
		}
		return this;
	}
	
	
	public SvnCommand checkout(String url,File destPath) {
		element("checkout");
		element("--force");
		protectElement(url);
		protectElement(destPath.getAbsolutePath());
		return this;
	}

	public SvnCommand commit(String message,File path) {
		element("commit");
		element("-m");
		protectElement(message);
		protectElement(path.getAbsolutePath());
		return this;
	}
	
}
