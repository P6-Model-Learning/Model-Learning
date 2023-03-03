import os
import fnmatch
import datetime
from systemd import journal
import json
import select


class Reader:
    def __int__(self):
        pass

    def reader(self) -> list:
        boards = []

        for root, dirs, files in os.walk(os.getcwd()):
            if root not in boards and (os.path.basename(root)).endswith('.prevas.dk') :
                boards.append(((os.path.basename(root)).split('-dut')[0], os.path.relpath(root, os.getcwd())))
        
        return boards
    
    def parseData(self, path: tuple):
        data = []
        i = 0

        for root, dirs, files in os.walk(path[1]):
            path = root.split(os.path.sep)
            for file in files:
                if file.startswith('system.journal') and file.endswith('.journal'):
                    j = journal.Reader(path = root)
                    j.get_next(skip=1)
                    j.log_level(level=7)
                    j.add_match("SYSLOG_IDENTIFIER=systemd")
                    data.append(("trace" + str(i), (file,[entry["MESSAGE"] for entry in j])))
                    i += 1

        return data

    def print_dir(self):
        for root, dirs, files in os.walk(os.pardir):
            path = root.split(os.sep)
            for file in files:
                if file.startswith("system.journal") and file.endswith(".journal"):
                    print((len(path) - 1) * '---', os.path.basename(root))
                    print((len(path) - 1) * '   ' + ' |__', file)
