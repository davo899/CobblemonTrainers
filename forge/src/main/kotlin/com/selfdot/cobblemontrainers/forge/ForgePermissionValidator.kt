package com.selfdot.cobblemontrainers.forge

import com.cobblemon.mod.common.api.permission.Permission
import com.selfdot.cobblemontrainers.command.permission.PermissionValidator
import com.selfdot.cobblemontrainers.command.permission.TrainersPermissions
import net.minecraft.command.CommandSource
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.Identifier
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.server.permission.PermissionAPI
import net.minecraftforge.server.permission.events.PermissionGatherEvent
import net.minecraftforge.server.permission.nodes.PermissionNode
import net.minecraftforge.server.permission.nodes.PermissionTypes

class ForgePermissionValidator : PermissionValidator {

    private val nodes = hashMapOf<Identifier, PermissionNode<Boolean>>()

    init {
        MinecraftForge.EVENT_BUS.addListener<PermissionGatherEvent.Nodes> {
            event -> event.addNodes(
                TrainersPermissions.all().map { permission ->
                    val node = PermissionNode(
                        permission.identifier,
                        PermissionTypes.BOOLEAN,
                        { player, _, _ -> player?.hasPermissionLevel(permission.level.numericalValue) == true }
                    )
                    this.nodes[permission.identifier] = node
                    node
                }
            )
        }
    }

    private fun findNode(permission: Permission) = this.nodes[permission.identifier]

    private fun extractPlayerFromSource(source: CommandSource) =
        if (source is ServerCommandSource) source.player else null

    override fun hasPermission(source: CommandSource, permission: Permission): Boolean {
        val player = this.extractPlayerFromSource(source) ?:
            return source.hasPermissionLevel(permission.level.numericalValue)
        val node = this.findNode(permission) ?: return source.hasPermissionLevel(permission.level.numericalValue)
        return PermissionAPI.getPermission(player, node)
    }

}
