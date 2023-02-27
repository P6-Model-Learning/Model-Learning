import glob
import os
from systemd import journal


class Reader:
    dataPath = ""
    fileName = ""
    fileExtension = ""

    def __init__(self, dataPath, fileName, fileExtension) -> None:
        self.dataPath = dataPath
        self.fileName = fileName
        self.fileExtension = fileExtension

    os.chdir(dataPath)
    for file in glob.glob(fileName):
        if file.endswith(fileExtension):
            # do some parsing magic
            pass