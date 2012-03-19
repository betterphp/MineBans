Copyright (C) 2012 Jacek Kuzemczak (wide_load) <support@minebans.com>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.



Introduction

Put simply, MineBans is a global banning system for Minecraft servers.
We offer a way for server admins to publish the player bans they make
on their servers to our database. The owners of other servers can then
use this information to decide if a player that just joined their server
should be allowed to stay.


Contributing / Pull Requests

Something you should know before submitting a pull request is that we
use the commit messages to generate the changelog on the downloads page.
This means two things.

Thing 1.
	
	The commit message must start with the version number in the format
	
	v0.1.1 Commit message here
	
	This ultimately means that you have to decide on a version number,
	If you are making an improvement to the code without changing the
	overall functionality take the last digit and increment it by 1. If
	you are adding a new feature or making a more significant change
	increment the second digit. The first should never be changed. 

Thing 2.

	The message should be free of technical jargon, try to explain the
	effect instead.