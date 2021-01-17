# -*- coding: utf-8 -*-
# @Time    : 2021/1/12 10:48
# @Author  : AWAYASAWAY
# @File    : Solution1.py.py
# @IDE     : PyCharm


from operator import itemgetter


class Vrp():

    # -----------initial---------------------

    def __init__(self, distanceLimit):
        self.mans = 49  # customer
        self.tons = 100  # load
        self.distanceLimit = distanceLimit  # max distance for vehicle, better
        self.distance = []  # distance matrix
        self.q = []  # 49
        self.savings = []
        self.Routes = []
        self.Cost = 0  # distance

    # -----------dataInput---------------------

    def dataInput(self):
        location = []

        with open(r'DEMAND_SECTION.txt') as f:
            for line in f:
                if line == '\n':
                    line = line.strip('\n')
                else:
                    line = ((line.strip('\n').strip(' ')).strip(' ')).split(' ')
                    self.q.append(float(line[1]))
        with open(r'NODE_COORD_SECTION.txt') as f:
            for line in f:
                if line == '\n':
                    line = line.strip('\n')
                else:
                    line = ((line.strip('\n').strip(' ')).strip(' ')).split(' ')

                    location.append(line)
        n = len(location)
        distance = [[0 for col in range(n)] for raw in range(n)]
        for i in range(n):
            for j in range(n):
                x = pow(float(location[i][1]) - float(location[j][1]), 2)
                y = pow(float(location[i][2]) - float(location[j][2]), 2)
                distance[i][j] = pow(x + y, 0.5)
        self.distance = distance

    # -----------saving algorithm---------------------

    def savingsAlgorithms(self):
        saving = 0
        for i in range(1, len(self.q)):
            self.Routes.append([i])

        for i in range(1, len(self.Routes) + 1):  # save Sij = Ci0 + C0j - Cij
            for j in range(1, len(self.Routes) + 1):
                if i == j:
                    pass
                else:
                    saving = (self.distance[i][0] + self.distance[0][j]) - self.distance[i][j]
                    self.savings.append([i, j, saving])

        self.savings = sorted(self.savings, key=itemgetter(2), reverse=True)  # sort by saving
        # for i in range(len(self.savings)):
        #     print(self.savings[i][0], '--', self.savings[i][1], "  ", self.savings[i][2])

        for i in range(len(self.savings)):
            startRoute = []
            endRoute = []
            routeDemand = 0
            for j in range(len(self.Routes)):
                if (self.savings[i][0] == self.Routes[j][-1]):
                    endRoute = self.Routes[j]
                elif (self.savings[i][1] == self.Routes[j][0]):
                    startRoute = self.Routes[j]

                if ((len(startRoute) != 0) and (len(endRoute) != 0)):
                    for k in range(len(startRoute)):
                        routeDemand += self.q[startRoute[k]]
                    for k in range(len(endRoute)):
                        routeDemand += self.q[endRoute[k]]
                    routeDistance = 0
                    routestore = [0] + endRoute + startRoute + [0]
                    for i in range(len(routestore) - 1):
                        # print(routestore[i],routestore[i+1])
                        # print(self.distance[routestore[i]][routestore[i+1]])
                        routeDistance += self.distance[routestore[i]][routestore[i + 1]]

                    # print(routestore,"== ==:",routeDistance)

                    if (routeDemand <= self.tons) and (routeDistance <= self.distanceLimit):  # distanceLimit
                        self.Routes.remove(startRoute)
                        self.Routes.remove(endRoute)
                        self.Routes.append(endRoute + startRoute)
                    break

        for i in range(len(self.Routes)):
            self.Routes[i].insert(0, 0)
            self.Routes[i].insert(len(self.Routes[i]), 0)

    # -----------printRoute---------------------

    def printRoutes(self):
        for i in self.Routes:
            global row
            costs = 0
            for j in range(len(i) - 1):
                costs += self.distance[i[j]][i[j + 1]]
            print("路线: ", i, "  路程:  ", costs)

    def calcCosts(self):
        for i in range(len(self.Routes)):
            for j in range(len(self.Routes[i]) - 1):
                self.Cost += self.distance[self.Routes[i][j]][self.Routes[i][j + 1]]
        print("\nTotal Distance: ", round(self.Cost, 3))
        return self.Cost

    # -----------Master.py---------------------

    def start(self):
        print("== == == == == == == == == == == == == == == 导入数据 == == == == == == == = == == == == == == == =")
        self.dataInput()
        print("== == == 距离表 == == ==")
        for i in self.distance:
            print(i)
        print("== == == 需求表 == == ==")
        print(self.q)
        print("== == == 限制条件 == == ==")
        print("车辆最大载重：", self.tons)
        print("车辆最长运输距离：", self.distanceLimit)
        print("== == == == == == == == == == == == == == == 节约度 == == == == == == == = == == == == == == == =")
        self.savingsAlgorithms()
        print("== == == == == == == == == == == == == == == 结果 == == == == == == == = == == == == == == == =")
        self.printRoutes()
        self.calcCosts()
        self.dataInput()


if __name__ == '__main__':
    for distLimit in range(150, 300, 10):
        vrp = Vrp(distanceLimit=distLimit)
        vrp.start()

