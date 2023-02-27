import glob
import os
from systemd import journal

dataPath = ""
filename = ""
fileExtension = ""

# traversal of file directory
os.chdir(dataPath)
for file in glob.glob(filename):
    if file.endswith(fileExtension):
        # do some magic parsing
        pass
