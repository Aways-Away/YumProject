# -*- coding: utf-8 -*-
# @Time    : 2021/1/12 14:23
# @Author  : AWAYASAWAY
# @File    : predict.py
# @IDE     : PyCharm

from numpy.random import *
from simpy import *
import pandas as pd
from queue import Queue
import numpy as np

data = pd.read_excel('./Data3.xlsx')

tableNums = 10
tableSize = 4


class Solution:
    def __init__(self, ratio=0.7):
        self.time = []
        self.ps = []
        self.money = []
        self.serviceTime = []
        self.ratio = 0.7

    def inputData(self):
        data = pd.read_excel('Data3.xlsx')
        print(len(data))
        for i in range(len(data)):
            self.time.append(data['time'][i])
            self.ps.append(data['ps'][i])
            self.money.append(data['money'][i])
            self.serviceTime.append(np.random.uniform(20, 40))
            # print(self.time, self.ps, self.money, self.serviceTime)
        return self.time, self.ps, self.money, self.serviceTime

    def haveSeat(self):
        tables = []
        for i in range(tableNums):
            tables.append(Queue(4))
        for i in range(tableNums):
            j = 0
            if self.ps <= tableSize and self.ps >= j:
                tables[i].append(j)
                j = j + 1
            while not tables[i].empty():
                # print("get[%d] = " % i, tables.get())
                i += 1

    def startMain(self):
        self.inputData()


if __name__ == '__main__':
    print("DEBUG")
    Solution().startMain()
