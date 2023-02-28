import os
import fnmatch


class Reader:
    def __int__(self):
        pass

    def readJorunals(self):
        pass

    def printDir(self):
        for root, dirs, files in os.walk(os.pardir):
            path = root.split(os.sep)
            print((len(path) - 1) * '---', os.path.basename(root))
            for file in files:
                if file.startswith("system.journal") and file.endswith(".journal"):
                    print((len(path) - 1) * '   ' + ' |__', file)

reader = Reader()
reader.printDir()