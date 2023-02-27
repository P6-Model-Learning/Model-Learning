from dataclasses import dataclass
from datetime import datetime


@dataclass
class syslogData:
    date: datetime
    message: str
    application: str
    board: str
    classification: int

    def __init__(self, date: datetime, message: str, classification: int, application: str = "", board: str = ""):
        self.message = message
        self.date = date
        self.classification = classification
        self.application = application
        self.board = board
