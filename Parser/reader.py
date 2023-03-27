import os
from systemd import journal
import json


class Reader:
    def getBoards(self) -> list:
        boards = []

        for root, dirs, files in os.walk(os.getcwd()):
            if root not in boards and (os.path.basename(root)).endswith('.prevas.dk'):
                boards.append(((os.path.basename(root))
                               .split('-dut')[0], os.path.relpath(root, os.getcwd())))
        return boards

    def parse(self, board:str):
        data = []

        for root, dirs, files in os.walk(os.getcwd()):
            path = root.split(os.path.sep)
            for file in files:
                if file.startswith('system.journal') and file.endswith('.journal'):
                    trace = []
                    j = journal.Reader(path=root)
                    if board != None: j.add_match("_HOSTNAME={board}".format(board = board))
                    j.get_next(skip=1)
                    j.log_level(level=7)
                    j.add_match("SYSLOG_IDENTIFIER=systemd")
                    for entry in j:
                        trace.append(entry)
                    data.append(trace)
        return list(filter(None, data))
    
    def parseSimple(self, board:str):
        data  =[]
        
        for root, dirs, files in os.walk(os.getcwd()):
            path  =root.split(os.path.sep)
            for file in files:
                if file.startswith('system.journal') and file.endswith('.journal'):
                    j = journal.Reader(path=root)
                    j.get_next(skip=1)
                    j.log_level(level=7)
                    j.add_match("SYSLOG_IDENTIFIER=systemd")
                    if board != None: j.add_match("_HOSTNAME={board}".format(board = board))
                    trace = []
                    for entry in j:
                        trace.append({
                            entry['MESSAGE'],
                            entry['_HOSTNAME'],
                            entry['__MONOTONIC_TIMESTAMP']
                        })
                    data.append(trace)
        return list(filter(None, data))

    def parseToJSON(self, data):
        return json.dumps(data, sort_keys=True, default=str)

    def parseToJSON(self, data):
        return json.dumps(data, sort_keys=True, default=str)

    def make_entry_dict(self, j: journal):
        pass

    def print_dir(self):
        for root, dirs, files in os.walk(os.pardir):
            path = root.split(os.sep)
            for file in files:
                if file.startswith("system.journal") and file.endswith(".journal"):
                    print((len(path) - 1) * '---', os.path.basename(root))
                    print((len(path) - 1) * '   ' + ' |__', file)
