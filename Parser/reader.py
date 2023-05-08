import os
from systemd import journal
import json
import re

class Reader:
    def parseBoards(self, boards, simple, init, directory):
        data = []
        start_string = "Test started"
        start_time = None
        
        for i in boards:
            data.append([])
            
        if directory is None: directory = os.getcwd()
        
        for root, dirs, files in os.walk(directory):
            path = root.split(os.path.sep)
            for file in files:
                if init:
                    if file.endswith('.journal') and file.startswith('system.journal'):
                        trace = self.__parseInitTrace(simple=simple, file=file, root=root)
                        if trace[1] != None:
                            data[boards.index(trace[1])].append(trace[0])
                else:
                    if file.endswith('.journal'):
                        trace = self.__parseTestTrace(simple=simple, file=file, root=root, start_string=start_string)
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
                             'SIMPLE_MESSAGE':self.__pruneMSG(entry['MESSAGE']),
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
                    entry['SIMPLE_MESSAGE'] = self.__pruneMSG(entry['MESSAGE'])
                    trace.append(entry)
                return trace, board
    
    def __parseTestTrace(self, simple, file, root, start_string):
        if file.endswith('.journal'):
            start_time = None
            board = None
            trace = []
            end_string = 'Received client request to rotate journal, rotating.'
            off = True
            j = journal.Reader(path=root)
            j.get_next(skip=1)
            j.log_level(level=7)
            if simple:
                for entry in j:
                    if entry['MESSAGE'] == start_string: off = False
                    if off: continue
                    if board == None: board = entry['_HOSTNAME']
                    if start_time == None: start_time = entry['__MONOTONIC_TIMESTAMP'][0]
                    entry['TIMEDELTA'] = (entry['__MONOTONIC_TIMESTAMP'][0] - start_time).total_seconds()
                    entry = {'MESSAGE':entry['MESSAGE'],
                             'SIMPLE_MESSAGE': self.__pruneMSG(entry['MESSAGE']),
                             '_HOSTNAME':entry['_HOSTNAME'],
                             '__MONOTONIC_TIMESTAMP':entry['__MONOTONIC_TIMESTAMP'],
                             'TIMEDELTA':entry['TIMEDELTA']}
                    trace.append(entry)
                    if entry['MESSAGE'] == end_string: off = True
                return trace, board
            else:
                for entry in j:
                    if entry['MESSAGE'] == start_string: off = False
                    if off: continue
                    if board == None: board = entry['_HOSTNAME']
                    if start_time == None: start_time = entry['__MONOTONIC_TIMESTAMP'][0]
                    entry['TIMEDELTA'] = (entry['__MONOTONIC_TIMESTAMP'][0] - start_time).total_seconds()
                    entry['SIMPLE_MESSAGE'] = self.__pruneMSG(entry['MESSAGE'])
                    trace.append(entry)
                    if entry['MESSAGE'] == end_string: off = True
                return trace, board
    
    def __pruneMSG(self, msg):
        ip_daemon_regex = re.compile('@[0-9]*-(([0-9]{1,3}\.){3}[0-9]{1,3}:[0-9]*-?)*')
        ip_regex = re.compile('[(]?(1-)?(?:[0-9]{1,3}\.){3}[0-9]{1,3}[:][0-9]*-*[)]?')
        time_regex = re.compile('(in)\s[0-9]*.[0-9]*s.*')
        sys_mode_regex = re.compile('(in system mode )[(](\s*([-]|[+])\w*\s*)*\s*(\w*[-]\w*[=]\w*)[)]')
        if re.search(ip_daemon_regex, msg): #Goes first as @ is an identifier of hostname IP
            msg = re.sub(ip_daemon_regex, '@hostname', msg)
        if re.search(ip_regex, msg): 
            msg = re.sub(ip_regex, '', msg)
        if re.search(time_regex, msg): 
            msg =  re.sub(time_regex, '', msg)
        if re.search(sys_mode_regex, msg):
            msg = re.sub(sys_mode_regex, '', msg)
        return msg

    def test_data(directory):
        off = True
        for root, dirs, files in os.walk(directory):
            path = root.split(os.path.sep)
            for file in files:
                j = journal.Reader(path=root)
                j.get_next(skip=1)
                for entry in j:
                    if entry['MESSAGE'] == 'Test started': off = False
                    if off == True: continue
                    print(entry['MESSAGE'])
                    if entry['MESSAGE'] == 'Received client request to rotate journal, rotating.': off = True
    
    def parseToJSON(self, data):
        return json.dumps(data, sort_keys=True, default=str, indent=4)

x = Reader
x.test_data(r'/home/claesm/Documents/code/sw6/bachelor_project/P6/p6-initdata/data/BBBTests.prevas.dk')