import os
from systemd import journal
import json

class JournalParser:    
    startStr = "Test start line inserted"
    endStr = "Active journal rotated"

    def getBoards(self):
        pass

    def parse(self, board:str):
        data = []
        start_time = None
        for root, dirs, files in os.walk(os.getcwd()):
            for file in files:
                if file.endswith('.journal'):
                    trace = []
                    j = journal.Reader(path=root)
                    if board != None: j.add_match("_HOSTNAME={board}".format(board = board))
                    j.get_next(skip=1)
                    j.log_level(level=7)
                    for entry in j:
                        if entry['MESSAGE'] == self.startStr:
                            start_time = entry['_SOURCE_MONOTONIC_TIMESTAMP']
                            entry['TIMEDELTA'] = entry['_SOURCE_MONOTONIC_TIMESTAMP'] - start_time
                            trace.append(entry)
                        elif start_time != None:
                            entry['TIMEDELTA'] = entry['_SOURCE_MONOTONIC_TIMESTAMP'] - start_time
                            trace.append(entry)
                    data.append(trace)
                    startTime = None
        return list(filter(None, data))
    
    def parseSimple(self, board:str):
        data = []
        start_time = None
        for root, dirs, files in os.walk(os.getcwd()):
            for file in files:
                if file.endswith('.journal'):
                    trace = []
                    j = journal.Reader(path=root)
                    if board != None: j.add_match("_HOSTNAME={board}".format(board = board))
                    j.get_next(skip=1)
                    j.log_level(level=7)
                    for entry in j:
                        if entry['MESSAGE'] == self.startStr:
                            start_time = entry['_SOURCE_MONOTONIC_TIMESTAMP']
                            entry['TIMEDELTA'] = start_time
                            trace.append({
                                entry['_SOURCE_MONOTONIC_TIMESTAMP'],
                                entry['TIMEDELTA'],
                                entry['MESSAGE'],
                                entry['_HOSTNAME']
                            })
                        elif start_time != None:
                            entry['TIMEDELTA'] = entry['_SOURCE_MONOTONIC_TIMESTAMP'] - start_time
                            trace.append({
                                entry['_SOURCE_MONOTONIC_TIMESTAMP'],
                                entry['TIMEDELTA'],
                                entry['MESSAGE'],
                                entry['_HOSTNAME']
                            })
                    data.append(trace)
                    start_time = None
        return list(filter(None, data))
    
    def getBoards(self) -> list:
        boards = []

        for root, dirs, files in os.walk(os.getcwd()):
            if root not in boards and (os.path.basename(root)).endswith('.prevas.dk'):
                boards.append(((os.path.basename(root))
                               .split('-dut')[0], os.path.relpath(root, os.getcwd())))
        return boards
    
    def parseToJSON(self, data):
        return json.dumps(data, sort_keys=True, default=str)
