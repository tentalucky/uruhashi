package mahoroba.uruhashi.presentation.exClasses

import android.graphics.Path
import mahoroba.uruhashi.common.Point2D

class PathEx : Path() {
    private val points = ArrayList<Point2D>()
    private val lines = ArrayList<Pair<Point2D, Point2D>>()

    override fun reset() {
        super.reset()
        points.clear()
        lines.clear()
    }

    override fun rewind() {
        super.rewind()
        points.clear()
        lines.clear()
    }

    override fun moveTo(x: Float, y: Float) {
        super.moveTo(x, y)
        points.add(Point2D(x, y))
    }

    override fun lineTo(x: Float, y: Float) {
        super.lineTo(x, y)
        points.add(Point2D(x, y))
        lines.add(Pair(points[points.size - 2], points[points.size -1]))
    }

    override fun close() {
        super.close()
        points.add(Point2D(points.first().x, points.first().y))
        lines.add(Pair(points[points.size - 2], points[points.size -1]))
    }

    override fun set(src: Path) {
        if (!(src is PathEx))
            throw Exception("The set method of PathEx class does not accept Path class argument. Set PathEx class object.")

        super.set(src)

        points.clear()
        for (it in src.points) { points.add(it.copy()) }

        lines.clear()
        for (it in src.lines) { lines.add(it.copy()) }
    }

    override fun offset(dx: Float, dy: Float) {
        super.offset(dx, dy)

        for (i in 0 until points.size) {
            points[i] = Point2D(points[i].x + dx, points[i].y + dy)
        }
        for (i in 0 until lines.size) {
            lines[i] = Pair(
                Point2D(lines[i].first.x + dx, lines[i].first.y + dy),
                Point2D(lines[i].second.x + dx, lines[i].second.y + dy))
        }
    }

    fun contains(x: Float, y: Float): Boolean {
        var across = 0

        for (it in lines) {
            if (it.first.y <= y && y < it.second.y || it.second.y <= y && y < it.first.y) {
                val vt = (y - it.first.y) / (it.second.y - it.first.y)
                if (x < it.first.x + (it.second.x - it.first.x) * vt) { across++ }
            }
        }

        return across % 2 == 1
    }
}