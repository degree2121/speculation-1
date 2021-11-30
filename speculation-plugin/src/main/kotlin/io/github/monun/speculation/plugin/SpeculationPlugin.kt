package io.github.monun.speculation.plugin

import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import io.github.monun.speculation.paper.PaperGameProcess
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

// TODO 주사위를 묶음으로 처리
// TODO 땅 금액 설정
// TODO 인수
// TODO 땅 매각
// TODO 겜블
// TODO 감옥
// TODO 페스티벌
// TODO 포탈
// TODO 국세청
// TODO 이벤트

class SpeculationPlugin : JavaPlugin() {
    var process: PaperGameProcess? = null
        private set

    override fun onEnable() {
        kommand {
            register("speculation") {
                permission("speculation.commands")
                then("start") {
                    then("world" to dimension(), "players" to players(), "teamMatch" to bool()) {
                        executes {
                            val world: World by it
                            val players: Collection<Player> by it
                            val teamMatch: Boolean by it

                            kotlin.runCatching {
                                startProcess(world, players.toSet(), teamMatch)
                            }.onFailure { exception ->
                                if (exception is IllegalStateException) {
                                    feedback(Component.text(exception.message ?: "").color(NamedTextColor.RED))
                                } else {
                                    throw exception
                                }
                            }
                        }
                    }
                }
                then("stop") {
                    executes {
                        kotlin.runCatching {
                            stopProcess()
                        }.onFailure { exception ->
                            if (exception is IllegalArgumentException) {
                                feedback(Component.text(exception.message ?: "").color(NamedTextColor.RED))
                            } else {
                                throw exception
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDisable() {
        runCatching { stopProcess() }
    }

    fun startProcess(world: World, players: Set<Player>, teamMatch: Boolean): PaperGameProcess {
        check(process == null) { "process already running" }

        return PaperGameProcess(this, world).apply {
            register(players, teamMatch)
        }.also {
            process = it
        }
    }

    fun stopProcess(): PaperGameProcess {
        val process = requireNotNull(process) { "process is not running" }
        process.unregister()
        this.process = null

        return process
    }
}