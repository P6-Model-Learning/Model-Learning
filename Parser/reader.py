import os
from systemd import journal
import json
import datetime


class Reader:
    def getBoards(self) -> list:
        boards = []

        for root, dirs, files in os.walk(os.getcwd()):
            if root not in boards and (os.path.basename(root)).endswith('.prevas.dk'):
                boards.append(((os.path.basename(root))
                               .split('-dut')[0]))
        return boards

    def parseBoards(self, boards, simple, init):
        data = []
        boards = self.getBoards()
        start_string = None
        start_time = None
        
        for i in boards:
            data.append([])
        
        for root, dirs, files in os.walk(os.getcwd()):
            path = root.split(os.path.sep)
            for file in files:
                if file.endswith('.journal') and file.startswith('system.journal'):
                    trace = self.__parseInitTrace(simple=simple, file=file, root=root)
                    if trace[1] != None:
                        data[boards.index(trace[1])].append(trace[0])
        return data
        
    def __parseInitTrace(self, simple, file, root):
        if file.startswith('system.journal') and file.endswith('.journal'):
            start_time = None
            board = None
            trace = []
            j = journal.Reader(path=root)
            j.get_next(skip=1)
            j.log_level(level=7)
            j.add_match("SYSLOG_IDENTIFIER=systemd")
            if simple:
                for entry in j:
                    if board == None: board = entry['_HOSTNAME']
                    if start_time == None: start_time = entry['__MONOTONIC_TIMESTAMP'][0]
                    entry['TIMEDELTA'] = (entry['__MONOTONIC_TIMESTAMP'][0] - start_time).total_seconds()
                    entry = {'MESSAGE':entry['MESSAGE'],
                             '_HOSTNAME':entry['_HOSTNAME'],
                             '__MONOTONIC_TIMESTAMP':entry['__MONOTONIC_TIMESTAMP'],
                             'TIMEDELTA':entry['TIMEDELTA']}
                    trace.append(entry)
                return trace, board
            else:
                for entry in j:
                    if board == None: board = entry['_HOSTNAME']
                    if start_time == None: start_time = entry['__MONOTONIC_TIMESTAMP'][0]
                    entry['TIMEDELTA'] = (entry['__MONOTONIC_TIMESTAMP'][0] - start_time).total_seconds()
                    trace.append(entry)
                return trace, board

    def parseToJSON(self, data):
        return json.dumps(data, sort_keys=True, default=str, indent=4)

    def print_dir(self):
        for root, dirs, files in os.walk(os.pardir):
            path = root.split(os.sep)
            for file in files:
                if file.startswith("system.journal") and file.endswith(".journal"):
                    print((len(path) - 1) * '---', os.path.basename(root))
                    print((len(path) - 1) * '   ' + ' |__', file)